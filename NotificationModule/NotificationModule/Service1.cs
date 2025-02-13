using Google.Cloud.Firestore;
using Google.Cloud.Location;
using Newtonsoft.Json;
using NotificationModule.Objects;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration;
using System.Data;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.ServiceProcess;
using System.Text;
using System.Threading.Tasks;
using System.Timers;

namespace NotificationModule
{
    public partial class Service1 : ServiceBase
    {

        Timer timer = new Timer();
        private static object _intervalSync = new object();
        public Service1()
        {
            InitializeComponent();
        }
        public void PushNotification(string fcmtoken, string content, string title)
        {
            WebRequest tRequest = WebRequest.Create("https://fcm.googleapis.com/fcm/send");
            tRequest.Method = "post";
            //serverKey - Key from Firebase cloud messaging server  
            tRequest.Headers.Add(string.Format("Authorization: key={0}", "AAAAYwWU_3M:APA91bHq5nCd7QWrPQD2ImogA971z3rj6StpH2RFMN7Vhp-YactZqXZVkBmmu0JyNM--mD7v78exDM6svs_Y1r9M-zYfzJktJyBTloLbdK2mfsjqBKZKmouZLZeTBs4EGsMlw8TvNC0p"));
            //Sender Id - From firebase project setting  
            tRequest.Headers.Add(string.Format("Sender: id={0}", "425295413107"));
            tRequest.ContentType = "application/json";
            var payload = new object();
            payload = new
            {
                to = fcmtoken,
                priority = "high",
                content_available = true,
                notification = new
                {
                    body = content,
                    title = title,
                    badge = 1,
                },
                data = new
                {
                    type = "alert",

                }

            };

            string postbody = JsonConvert.SerializeObject(payload).ToString();
            Byte[] byteArray = Encoding.UTF8.GetBytes(postbody);
            tRequest.ContentLength = byteArray.Length;
            using (Stream dataStream = tRequest.GetRequestStream())
            {
                dataStream.Write(byteArray, 0, byteArray.Length);
                using (WebResponse tResponse = tRequest.GetResponse())
                {
                    using (Stream dataStreamResponse = tResponse.GetResponseStream())
                    {
                        if (dataStreamResponse != null) using (StreamReader tReader = new StreamReader(dataStreamResponse))
                            {
                                String sResponseFromServer = tReader.ReadToEnd();
                                WriteToFile(sResponseFromServer);
                                //result.Response = sResponseFromServer;
                            }
                    }
                }
            }

        }
        private async Task<Recommendation> GetAndConvertToJSON(GeoPoint location,string userId)
        {
            // Replace 'your_api_url_here' with the actual URL of the API you want to call.
            var value = ConfigurationManager.AppSettings["Url"].ToString();
            string apiUrl = value+"/api/recommend?userId="+userId+"&latitude="+location.Latitude+"&longitude="+location.Longitude+"&localTime=12:00%20PM%20MST&dateFilter=TODAY&distanceFilter=LESS_THAN_10_MILES";
            try
            {
                using (HttpClient client = new HttpClient())
                {
                    // Make a GET request
                    HttpResponseMessage response = await client.GetAsync(apiUrl);

                    if (response.IsSuccessStatusCode)
                    {
                        // Read the content as a string
                        string jsonString = await response.Content.ReadAsStringAsync();
                        WriteToFile("\nURL Response:" + jsonString);
                        // Deserialize the JSON string to your object
                        RecommendationResponse result = JsonConvert.DeserializeObject<RecommendationResponse>(jsonString);

                        // Now 'result' contains your data in the strongly-typed object
                        return result.recommendations[0];
                    }
                    else
                    {
                        Console.WriteLine($"Error: {response.StatusCode}");
                        return new Recommendation();
                    }
                }
            }catch (Exception ex)
            {
                WriteToFile("Exception in GetAndConvertToJSON\n" + ex.ToString());
                return new Recommendation();
            }

        }

        private async void GenerateNotifications()
        {
            string path = AppDomain.CurrentDomain.BaseDirectory + "servicekey.json";
            Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", path);
            WriteToFile("\n Generate Notification Started");
            try
            {
                WriteToFile("\nInside Send Notifications Function");
                var db = FirestoreDb.Create("fir-auth-7e9d6");
                var colref = await db.Collection("users").GetSnapshotAsync();
                foreach(var doc in colref.Documents)
                {
                    var dict = doc.ToDictionary();
                    WriteToFile("\n user:"+doc.Id);
                    if (dict.ContainsKey("Location")&&dict.ContainsKey("fcmtoken"))
                    {
                        var location = (GeoPoint)dict["Location"];
                        var response=await GetAndConvertToJSON(location, doc.Id);
                        
                        if(response!=new Recommendation())
                        {
                            var title="We know you are interested in "+ response.event1.eventType+" events";
                            var description=response.event1.eventName+" is happening near. Tap now to know more";
                            PushNotification(dict["fcmtoken"].ToString(), description, title);
                        }
                    }
                    else
                    {
                        WriteToFile("\n There is a missing Location Field or fcm token for user " + doc.Id);
                    }
                }
            }
            catch (Exception e)
            {
                WriteToFile("Exception in GenerateNotifications\n" + e.ToString());
            }
        }
        protected override void OnStart(string[] args)
        {
            
            WriteToFile("Service is started at " + DateTime.Now);
            GenerateNotifications();
            timer.Elapsed += new ElapsedEventHandler(OnElapsedTime);
            timer.Interval = 86400000; 
            timer.Enabled = true;
        }

        protected override void OnStop()
        {
            WriteToFile("Service is stopped at " + DateTime.Now);
        }

        private void OnElapsedTime(object source, ElapsedEventArgs e)
        {
            WriteToFile("Service is recall at " + DateTime.Now);
            if (System.Threading.Monitor.TryEnter(_intervalSync))
            {
                try
                {
                    GenerateNotifications();
                }catch (Exception ex)
                {
                    WriteToFile("Exception in OnElapsedTime \n " + ex.InnerException);
                }
                finally
                {
                    // Make sure Exit is always called
                    System.Threading.Monitor.Exit(_intervalSync);
                }
            }
            else
            {
                //Previous interval is still in progress.
            }

        }
        public void WriteToFile(string Message)
        {
            string path = AppDomain.CurrentDomain.BaseDirectory + "\\Logs";
            if (!Directory.Exists(path))
            {
                Directory.CreateDirectory(path);
            }
            string filepath = AppDomain.CurrentDomain.BaseDirectory + "\\Logs\\ServiceLog_" + DateTime.Now.Date.ToShortDateString().Replace('/', '_') + ".txt";
            if (!File.Exists(filepath))
            {
                // Create a file to write to.   
                using (StreamWriter sw = File.CreateText(filepath))
                {
                    sw.WriteLine(Message);
                }
            }
            else
            {
                using (StreamWriter sw = File.AppendText(filepath))
                {
                    sw.WriteLine(Message);
                }
            }
        }
    }
}
