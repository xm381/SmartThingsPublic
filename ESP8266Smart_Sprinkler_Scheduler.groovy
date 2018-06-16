/**
*  This is a start to porting the Arduino and SmartShield based
*  Irrigation Controllers to an ESP8266 based controller
*  Author:  Aaron Nienhuis (aaron.nienhuis@gmail.com)
*
*  Date:  2017-04-07
*  Copyright 2017 Aaron Nienhuis
*  
*  
* 
*  Smart Sprinkler Scheduler SmartApp
*  Compatible with up to 24 Zones
*
*  ESP8266 port based on the extensive previous work of:
*  Author: Stan Dotson (stan@dotson.info) and Matthew Nichols (matt@nichols.name)
*
*  Portions of this work previously copyrighted by Stan Dotson and Matthew Nichols
*
*   Some code and concepts incorporated from other projects by:
*  Eric Maycock
*
*  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License. You may obtain a copy of the License at:
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.
*
*
**/

/**
*   MRW Mods 2018
*   Split original 24 zone time inputs into three seperate groups of 8 to allow three programed schedules, prog2 and prog3 not yet implemented
*   Rearrange preferences screens to support zone time changes
*   Implement odd/even water days; add odd/even preference input options, add odd/even logic in function isWateringDay()
*   Change function isWeatherDelay(); changed rainGauge.round(2) to rainGuage due to throwing java error based on what data was returned from Weather Underground
**/

definition(
    name: "Smart Sprinkler Scheduler",
    namespace: "anienhuis",
    author: "aaron.nienhuis@gmail.com",
    description: "Child SmartApp to create schedules for ESP8266 based Smart Sprinkler Controllers",
    version: "1.0.1",
    parent: "anienhuis:Smart Sprinkler (Connect)",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/water_moisture.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/water_moisture@2x.png"
)

preferences {
    

    page(name: "sprinklerPage", title: "Sprinkler Controller Setup", nextPage: "schedulePage", uninstall: true) {
        section("Select your sprinkler controller...") {
            input "switches", "capability.switch", multiple: false
        }
    }

    page(name: "schedulePage", title: "Create An Irrigation Schedule", nextPage: "weatherPage", uninstall: false) {

        section ("Program 1", hideable: true, hidden: false) {
            input (
            name: "wateringDays",
            type: "enum",
            title: "Water on which days?",
            required: false,
            multiple: true, // This must be changed to false for development (known ST IDE bug)
// MRW added 'Even' and 'Odd' options
            metadata: [values: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday', 'Even', 'Odd']])
        	
            input "days", "number", title: "Minimum days between waterings", description: "minimum # days between watering", defaultValue: "1", required: false
        	
            input name: "waterTimeOne", title:"Start watering at what time", type: "time", required: true
 
            title: "Zone Times"
            input "prog1zone1", "string", title: "Program 1 - Zone 1 Time", description: "minutes", multiple: false, required: false
            input "prog1zone2", "string", title: "Program 1 - Zone 2 Time", description: "minutes", multiple: false, required: false
            input "prog1zone3", "string", title: "Program 1 - Zone 3 Time", description: "minutes", multiple: false, required: false
            input "prog1zone4", "string", title: "Program 1 - Zone 4 Time", description: "minutes", multiple: false, required: false
            input "prog1zone5", "string", title: "Program 1 - Zone 5 Time", description: "minutes", multiple: false, required: false
            input "prog1zone6", "string", title: "Program 1 - Zone 6 Time", description: "minutes", multiple: false, required: false
            input "prog1zone7", "string", title: "Program 1 - Zone 7 Time", description: "minutes", multiple: false, required: false
            input "prog1zone8", "string", title: "Program 1 - Zone 8 Time", description: "minutes", multiple: false, required: false
        }

        section ("Program 2 (not yet implemented)", hideable: true, hidden: true) {
            input (
            name: "prog2wateringDays",
            type: "enum",
            title: "Water on which days?",
            required: false,
            multiple: true, // This must be changed to false for development (known ST IDE bug)
            metadata: [values: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday', 'Even', 'Odd']])
	
            input "prog2days", "number", title: "Minimum days between waterings", description: "minimum # days between watering", defaultValue: "1", required: false
    
            input name: "waterTimeTwo",  title:"Start watering at what time", type: "time", required: false

            title: "Zone Times"
            input "prog2zone1", "string", title: "Program 2 - Zone 1 Time", description: "minutes", multiple: false, required: false
            input "prog2zone2", "string", title: "Program 2 - Zone 2 Time", description: "minutes", multiple: false, required: false
            input "prog2zone3", "string", title: "Program 2 - Zone 3 Time", description: "minutes", multiple: false, required: false
            input "prog2zone4", "string", title: "Program 2 - Zone 4 Time", description: "minutes", multiple: false, required: false
            input "prog2zone5", "string", title: "Program 2 - Zone 5 Time", description: "minutes", multiple: false, required: false
            input "prog2zone6", "string", title: "Program 2 - Zone 6 Time", description: "minutes", multiple: false, required: false
            input "prog2zone7", "string", title: "Program 2 - Zone 7 Time", description: "minutes", multiple: false, required: false
            input "prog2zone8", "string", title: "Program 2 - Zone 8 Time", description: "minutes", multiple: false, required: false
        }

        section ("Program 3 (not yet implemented)", hideable: true, hidden: true) {
            input (
            name: "prog3wateringDays",
            type: "enum",
            title: "Water on which days?",
            required: false,
            multiple: true, // This must be changed to false for development (known ST IDE bug)
            metadata: [values: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday', 'Even', 'Odd']])
      	
            input "prog3days", "number", title: "Minimum days between waterings", description: "minimum # days between watering", defaultValue: "1", required: false
         
            input name: "waterTimeThree",  title:"Start watering at what time", type: "time", required: false

			title: "Zone Times"
            input "prog3zone1", "string", title: "Program 3 - Zone 1 Time", description: "minutes", multiple: false, required: false
            input "prog3zone2", "string", title: "Program 3 - Zone 2 Time", description: "minutes", multiple: false, required: false
            input "prog3zone3", "string", title: "Program 3 - Zone 3 Time", description: "minutes", multiple: false, required: false
            input "prog3zone4", "string", title: "Program 3 - Zone 4 Time", description: "minutes", multiple: false, required: false
            input "prog3zone5", "string", title: "Program 3 - Zone 5 Time", description: "minutes", multiple: false, required: false
            input "prog3zone6", "string", title: "Program 3 - Zone 6 Time", description: "minutes", multiple: false, required: false
            input "prog3zone7", "string", title: "Program 3 - Zone 7 Time", description: "minutes", multiple: false, required: false
            input "prog3zone8", "string", title: "Program 3 - Zone 8 Time", description: "minutes", multiple: false, required: false
        }

    }

    page(name: "weatherPage", title: "Virtual Weather Station Setup", nextPage: "scheduleName", uninstall: false) {
        
        section("Zip code or Weather Station ID to check weather...") {
            input "zipcode", "text", title: "Enter zipcode or or pws:stationid", required: false
        }
        
        section("Select which rain to add to your virtual rain guage...") {
            input "isYesterdaysRainEnabled", "boolean", title: "Yesterday's Rain", description: "Include?", defaultValue: "true", required: false
            input "isTodaysRainEnabled", "boolean", title: "Today's Rain", description: "Include?", defaultValue: "true", required: false
            input "isForecastRainEnabled", "boolean", title: "Today's Forecasted Rain", description: "Include?", defaultValue: "false", required: false
        }
       
        section("Skip watering if virutal rain guage totals more than... (default 0.5)") {
            input "wetThreshold", "decimal", title: "Inches?", defaultValue: "0.5", required: false
        }
        
        section("Run watering only if forecasted high temp (F) is greater than... (default 50)") {
            input "tempThreshold", "decimal", title: "Temp?", defaultValue: "50", required: false
        }
        
    }
    
    page(name: "scheduleName", title: "Schedule Name", install: true) {
        section("Preferences") {
            label name: "title", title: "Name this irrigation schedule...", required: false, multiple: false, defaultValue: "Irrigation Scheduler"
            input "isNotificationEnabled", "boolean", title: "Send Push Notification When Irrigation Starts", description: "Do You Want To Receive Push Notifications?", defaultValue: "true", required: false
            input "isRainGuageNotificationEnabled", "boolean", title: "Send Push Notification With Rain Guage Report", description: "Do You Want To Receive Push Notifications?", defaultValue: "false", required: false
        }
    }
}       

def installed() {
    scheduling()
    state.daysSinceLastWatering = [0,0,0]
}

def updated() {
    unschedule()
    scheduling()
    state.daysSinceLastWatering = [0,0,0]
}

// Scheduling
def scheduling() {
    schedule(waterTimeOne, "waterTimeOneStart")
    if (waterTimeTwo) {
        schedule(waterTimeTwo, "waterTimeTwoStart")
    }
    if (waterTimeThree) {
        schedule(waterTimeThree, "waterTimeThreeStart")
    }
}

def waterTimeOneStart() {
    state.currentTimerIx = 0
    scheduleCheck()
}
def waterTimeTwoStart() {
    state.currentTimerIx = 1
    scheduleCheck()
}
def waterTimeThreeStart() {
    state.currentTimerIx = 2
    scheduleCheck()
}

def scheduleCheck() {

    def schedulerState = switches?.latestValue("effect")?.toString() ?:"[noEffect]"
    log.info "Running Irrigation Schedule: ${app.label}"

    if (schedulerState == "onHold") {
        log.info("${app.label} sprinkler schedule on hold.")
        return
    } 
    
    if (schedulerState == "skip") { 
        // delay this watering and reset device.effect to noEffect
        schedulerState = "delay" 
        for(s in switches) {
            if("noEffect" in s.supportedCommands.collect { it.name }) {
                s.noEffect()
                log.info ("${app.label} skipped one watering and will resume normal operations at next scheduled time")
            }
        }
    }    
    
    if (schedulerState != "expedite") { 
        // Change to delay if wet or too cold
        schedulerState = isWeatherDelay() ? "delay" : schedulerState
    }

    if (schedulerState != "delay") {
        state.daysSinceLastWatering[state.currentTimerIx] = daysSince() + 1
    }
//    Next line is useful log statement for debugging why the smart app may not be triggering.
    log.info("${app.label} scheduler state: ${schedulerState}. Days since last watering: ${daysSince()}. Is watering day? ${isWateringDay()}. Enought time? ${enoughTimeElapsed(schedulerState)} ")

    if ((isWateringDay() && enoughTimeElapsed(schedulerState) && schedulerState != "delay") || schedulerState == "expedite") {
        state.daysSinceLastWatering[state.currentTimerIx] = 0
        def wateringAttempts = 1
        water(wateringAttempts)
    }
}

def isWateringDay() {
    if(!wateringDays) return true
// MRW add even/odd logic
    if (wateringDays.contains('Even') || wateringDays.contains('Odd')) {
        def daynum = new Date().format('dd', location.timeZone)
        int dayint = Integer.parseInt(daynum)
        if (wateringDays.contains('Odd') && dayint.toBigInteger().mod( 2 ) != 0) {
           log.info ("${app.label} Odd watering day")
            return true
        }
        else if (wateringDays.contains('Even') && dayint.toBigInteger().mod( 2 ) == 0) {
            log.info ("${app.label} Even watering day")
            return true
        }
    }
    else {
        def today = new Date().format("EEEE", location.timeZone)        
        if (wateringDays.contains(today)) {
            return true
        }
    }
    log.info "${app.label} watering is not scheduled for today"
    return false
}

def enoughTimeElapsed(schedulerState) {
    if(!days) return true
    return (daysSince() >= days)
}

def daysSince() {
    if(!state.daysSinceLastWatering) state.daysSinceLastWatering = [0,0,0]
    state.daysSinceLastWatering[state.currentTimerIx] ?: 0
}

def isWeatherDelay() { 
    log.info "${app.label} Is Checking The Weather"
    if (zipcode) {
        
        //add rain to virtual rain guage
        def rainGauge = 0
        def todaysInches
        def yesterdaysInches
        def forecastInches
        
        if (isYesterdaysRainEnabled.equals("true")) {        
            yesterdaysInches = wasWetYesterday()
            rainGauge = rainGauge + yesterdaysInches
        }

        if (isTodaysRainEnabled.equals("true")) {
            todaysInches=isWet()
            rainGauge = rainGauge + todaysInches
        }

        if (isForecastRainEnabled.equals("true")) {
            forecastInches = isStormy()
            rainGauge = rainGauge + forecastInches
        }
// MRW changed rainGauge.round(2) due to throwing java error      
        if (isRainGuageNotificationEnabled.equals("true")) {
                sendPush("Virtual rain gauge reads ${rainGauge} inches.\nToday's Rain: ${todaysInches} inches, \nYesterday's Rain: ${yesterdaysInches} inches, \nForecast Rain: ${forecastInches} inches")  
        }
// MRW changed rainGauge.round(2) due to throwing java error      
        log.info ("Virtual rain gauge reads ${rainGauge} inches")
        
 //     check to see if virtual rainguage exceeds threshold
        if (rainGauge > (wetThreshold?.toFloat() ?: 0.5)) {
            if (isNotificationEnabled.equals("true")) {
                sendPush("Skipping watering today due to precipitation.")    
            }
            log.info "${app.label} skipping watering today due to precipitation."
            for(s in switches) {
                if("rainDelayed" in s.supportedCommands.collect { it.name }) {
                    s.rainDelayed()
                    log.info "Watering is rain delayed for $s"
                }
            }
            return true
        }
        
        def maxThermometer = isHot()
        if (maxThermometer < (tempThreshold?.toFloat() ?: 0)) {
            if (isNotificationEnabled.equals("true")) {
                sendPush("Skipping watering: $maxThermometer forecast high temp is below threshold temp.")
            }
            log.info "${app.label} is skipping watering: temp is below threshold temp."
            return true
        }
     }
    return false
}

def safeToFloat(value) {
    if(value && value.isFloat()) return value.toFloat()
    return 0.0
}

def wasWetYesterday() {
    
    def yesterdaysWeather = getWeatherFeature("yesterday", zipcode)
    log.debug "Yesterday Weather: $yesterdaysWeather"
    def yesterdaysPrecip = yesterdaysWeather?.history?.dailysummary?.precipi?.toArray() 
    log.debug "Yesterday Precip: $yesterdaysPrecip"
    log.debug "Yesterday inches: $yesterdaysPrecip[0])"
    def yesterdaysInches= yesterdaysPrecip ? safeToFloat(yesterdaysPrecip[0]) : 0
    log.info("Yesterday's precipitation for $zipcode: $yesterdaysInches in")
    return yesterdaysInches    
}

def isWet() {

    def todaysWeather = getWeatherFeature("conditions", zipcode)
    def todaysPrecip = (todaysWeather?.current_observation?.precip_today_in)
    def todaysInches = todaysPrecip ? safeToFloat(todaysPrecip) : 0
    log.info("Today's precipitation for ${zipcode}: ${todaysInches} in")
    return todaysInches
}

def isStormy() {

    def forecastWeather = getWeatherFeature("forecast", zipcode)
    def forecastPrecip=forecastWeather.forecast.simpleforecast.forecastday.qpf_allday.in?.toArray()
    def forecastInches = forecastPrecip ? safeToFloat(forecastPrecip[0]) : 0
    log.info("Forecast precipitation for $zipcode: $forecastInches in")
    return forecastInches
}

def isHot() {

    def forecastWeather = getWeatherFeature("forecast", zipcode)
    def todaysTemps=forecastWeather.forecast.simpleforecast.forecastday.high.fahrenheit?.toArray()
    def todaysHighTemp = todaysTemps ? safeToFloat(todaysTemps[0]) : 50
    log.info("Forecast high temperature for $zipcode: $todaysHighTemp F")
    return todaysHighTemp
}

//send watering times over to the device handler
def water(attempts) {
    log.info ("Starting Irrigation Schedule: ${app.label}")
    if (isNotificationEnabled.equals("true")) {
            sendPush("${app.label} Is Starting Irrigation" ?: "null pointer on app name")
    }
    if(Prog1ZoneTimes()) {
        def zoneTimes = []
        for(int z = 1; z <= 8; z++) {
            def zoneTime = settings["zone${z}"]
            if(zoneTime) {
                zoneTimes += "${z}:${zoneTime}"
                log.info("Zone ${z} on for ${zoneTime} minutes")
            }
        }
        switches.OnWithZoneTimes(zoneTimes.join(","))
    } 
    else {
        switches.on()
    }
    if (attempts <2) {
        // developers note: runIn() appears to only call void methods
        runIn(20, isWateringCheckOnce)
    }
    else {
        runIn(20, isWateringCheckTwice)
    }
}

def isWateringCheckOnce() { 
    def switchCurrentState = switches.currentSwitch    
    if (switchCurrentState != "on") {
        log.info "${app.label} is unable to turn on irrigation system.  Trying a second time"
        def wateringAttempts = 2
        // try to start watering again
        water(wateringAttempts)
    }
}       

def isWateringCheckTwice() { 
    def switchCurrentState = switches.currentSwitch    
    if (switchCurrentState != "on") {
        switches.warning()
        sendPush("${app.label} did not start after two attempts.  Check system")
        log.info "WARNING: ${app.label} failed to water. Check your system"
    }  
}       

def Prog1ZoneTimes() {
    return prog1zone1 || prog1zone2 || prog1zone3 || prog1zone4 || prog1zone5 || prog1zone6 || prog1zone7 || prog1zone8
}

def Prog2ZoneTimes() {
    return prog2zone1 || prog2zone2 || prog2zone3 || prog2zone4 || prog2zone5 || prog2zone6 || prog2zone7 || prog2zone8
}

def Prog3ZoneTimes() {
    return prog3zone1 || prog3zone2 || prog3zone3 || prog3zone4 || prog3zone5 || prog3zone6 || prog3zone7 || prog3zone8
}
