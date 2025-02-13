const basePrompt = (preference) => {
  let systemPrompt = `
    # Directive
    Retrieve and present information about upcoming events based on user preferences and location. The input will be in the form of a dictionary, providing user preferences and location details. Ensure that the output is in JSON format and includes the following fields for each event:
    Do not suggest any past events.

    Event Type
    Event Date
    Event Time
    Event Location
    Event Name
    Additional Details (such as booking links, cost, etc.)
  
    
    Provide a JSON-formatted output with relevant event information based on the user's preferences and location.

    **RULES** 
    1. The output should be in json format, do not enclose it within strings
    2. The Event Type must strictly be the input preference, nothing other than that
    3. Do not add any void spaces in the JSON output, it should be strictly in JSON.
    4. The event date must be on or after today. Do not give any past events.
    5. Give atleast 5 output results.
    6. Do not return incomplete results. the output must strictly contain all the fields.
    7. Do not return results starting with "json". Strictly JSON objects/arrays.


    Example User Preferences and Location Dictionary:
    {
      "preference": "Concerts",
      "location": "Tempe, AZ"
    }

    Example of the expected json output:
    [
      {
        "Event Type": "Concert",
        "Event Date": "2023-11-26",
        "Event Time": "7:00 PM",
        "Event Location": "Tempe Beach Park Amphitheatre",
        "Event Name": "The Killers",
        "Additional Details": {
            "Booking Link": "https://www.ticketmaster.com/the-killers-tickets/artist/2087518",
            "Cost": "$50-$150"
        }
      },
      {
        "Event Type": "Concert",
        "Event Date": "2023-12-03",
        "Event Time": "8:00 PM",
        "Event Location": "The Marquee Theatre",
        "Event Name": "My Chemical Romance",
        "Additional Details": {
            "Booking Link": "https://www.ticketmaster.com/my-chemical-romance-tickets/artist/1806587",
            "Cost": "$50-$150"
        }
      }
  ]

    Here is the input:
    {
      "preference": ${preference},
      "location": "Tempe, AZ"
    }
    `;

  return systemPrompt;
};

module.exports = basePrompt;
