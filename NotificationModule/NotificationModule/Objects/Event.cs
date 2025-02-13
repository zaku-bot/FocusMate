using Google.Cloud.Location;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NotificationModule.Objects
{
    internal class Event
    {
        public string eventName { get; set; }
        public string eventType { get; set; }
        public string eventDate { get; set; }
        public string eventTime { get; set; }
        public string eventLocationName { get; set; }
        public EventLocation eventLocation { get; set; }
        public EventDetails additionalDetails { get; set; }
    }
}
