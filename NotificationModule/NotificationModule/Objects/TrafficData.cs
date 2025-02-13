using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NotificationModule.Objects
{
    internal class TrafficData
    {
        public double distance { get; set; }
        public long duration { get; set; }
        public long durationInTraffic { get; set; }
        public string trafficDetails { get; set; }
    }
}
