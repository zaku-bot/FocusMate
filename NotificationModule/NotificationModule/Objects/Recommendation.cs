using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NotificationModule.Objects
{
    internal class Recommendation
    {
        [JsonProperty("event")]
        public Event event1 { get; set; }
        public WeatherData weatherData { get; set; }
        public TrafficData trafficData { get; set; }
    }
}
