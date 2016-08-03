
//------------------------------------------------------------*********************************---------------------------------------//

/*
 * Date Format 1.2.3
 * (c) 2007-2009 Steven Levithan <stevenlevithan.com>
 * MIT license
 *
 * Includes enhancements by Scott Trenda <scott.trenda.net>
 * and Kris Kowal <cixar.com/~kris.kowal/>
 *
 * Accepts a date, a mask, or a date and a mask.
 * Returns a formatted version of the given date.
 * The date defaults to the current date/time.
 * The mask defaults to dateFormat.masks.default.
 */

(function(global) {
  'use strict';

  var dateFormat = (function() {
      var token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZWN]|'[^']*'|'[^']*'/g;
      var timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g;
      var timezoneClip = /[^-+\dA-Z]/g;

      // Regexes and supporting functions are cached through closure
      return function (date, mask, utc, gmt) {

        // You can't provide utc if you skip other args (use the 'UTC:' mask prefix)
        if (arguments.length === 1 && kindOf(date) === 'string' && !/\d/.test(date)) {
          mask = date;
          date = undefined;
        }

        date = date || new Date;

        if(!(date instanceof Date)) {
          date = new Date(date);
        }

        if (isNaN(date)) {
          throw TypeError('Invalid date');
        }

        mask = String(dateFormat.masks[mask] || mask || dateFormat.masks['default']);

        // Allow setting the utc/gmt argument via the mask
        var maskSlice = mask.slice(0, 4);
        if (maskSlice === 'UTC:' || maskSlice === 'GMT:') {
          mask = mask.slice(4);
          utc = true;
          if (maskSlice === 'GMT:') {
            gmt = true;
          }
        }

        var _ = utc ? 'getUTC' : 'get';
        var d = date[_ + 'Date']();
        var D = date[_ + 'Day']();
        var m = date[_ + 'Month']();
        var y = date[_ + 'FullYear']();
        var H = date[_ + 'Hours']();
        var M = date[_ + 'Minutes']();
        var s = date[_ + 'Seconds']();
        var L = date[_ + 'Milliseconds']();
        var o = utc ? 0 : date.getTimezoneOffset();
        var W = getWeek(date);
        var N = getDayOfWeek(date);
        var flags = {
          d:    d,
          dd:   pad(d),
          ddd:  dateFormat.i18n.dayNames[D],
          dddd: dateFormat.i18n.dayNames[D + 7],
          m:    m + 1,
          mm:   pad(m + 1),
          mmm:  dateFormat.i18n.monthNames[m],
          mmmm: dateFormat.i18n.monthNames[m + 12],
          yy:   String(y).slice(2),
          yyyy: y,
          h:    H % 12 || 12,
          hh:   pad(H % 12 || 12),
          H:    H,
          HH:   pad(H),
          M:    M,
          MM:   pad(M),
          s:    s,
          ss:   pad(s),
          l:    pad(L, 3),
          L:    pad(Math.round(L / 10)),
          t:    H < 12 ? 'a'  : 'p',
          tt:   H < 12 ? 'am' : 'pm',
          T:    H < 12 ? 'A'  : 'P',
          TT:   H < 12 ? 'AM' : 'PM',
          Z:    gmt ? 'GMT' : utc ? 'UTC' : (String(date).match(timezone) || ['']).pop().replace(timezoneClip, ''),
          o:    (o > 0 ? '-' : '+') + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
          S:    ['th', 'st', 'nd', 'rd'][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10],
          W:    W,
          N:    N
        };

        return mask.replace(token, function (match) {
          if (match in flags) {
            return flags[match];
          }
          return match.slice(1, match.length - 1);
        });
      };
    })();

  dateFormat.masks = {
    'default':               'ddd mmm dd yyyy HH:MM:ss',
    'shortDate':             'm/d/yy',
    'mediumDate':            'mmm d, yyyy',
    'longDate':              'mmmm d, yyyy',
    'fullDate':              'dddd, mmmm d, yyyy',
    'shortTime':             'h:MM TT',
    'mediumTime':            'h:MM:ss TT',
    'longTime':              'h:MM:ss TT Z',
    'isoDate':               'yyyy-mm-dd',
    'isoTime':               'HH:MM:ss',
    'isoDateTime':           'yyyy-mm-dd\'T\'HH:MM:sso',
    'isoUtcDateTime':        'UTC:yyyy-mm-dd\'T\'HH:MM:ss\'Z\'',
    'expiresHeaderFormat':   'ddd, dd mmm yyyy HH:MM:ss Z'
  };

  // Internationalization strings
  dateFormat.i18n = {
    dayNames: [
      'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat',
      'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'
    ],
    monthNames: [
      'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec',
      'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'
    ]
  };

function pad(val, len) {
  val = String(val);
  len = len || 2;
  while (val.length < len) {
    val = '0' + val;
  }
  return val;
}

/**
 * Get the ISO 8601 week number
 * Based on comments from
 * http://techblog.procurios.nl/k/n618/news/view/33796/14863/Calculate-ISO-8601-week-and-year-in-javascript.html
 *
 * @param  {Object} `date`
 * @return {Number}
 */
function getWeek(date) {
  // Remove time components of date
  var targetThursday = new Date(date.getFullYear(), date.getMonth(), date.getDate());

  // Change date to Thursday same week
  targetThursday.setDate(targetThursday.getDate() - ((targetThursday.getDay() + 6) % 7) + 3);

  // Take January 4th as it is always in week 1 (see ISO 8601)
  var firstThursday = new Date(targetThursday.getFullYear(), 0, 4);

  // Change date to Thursday same week
  firstThursday.setDate(firstThursday.getDate() - ((firstThursday.getDay() + 6) % 7) + 3);

  // Check if daylight-saving-time-switch occured and correct for it
  var ds = targetThursday.getTimezoneOffset() - firstThursday.getTimezoneOffset();
  targetThursday.setHours(targetThursday.getHours() - ds);

  // Number of weeks between target Thursday and first Thursday
  var weekDiff = (targetThursday - firstThursday) / (86400000*7);
  return 1 + Math.floor(weekDiff);
}

/**
 * Get ISO-8601 numeric representation of the day of the week
 * 1 (for Monday) through 7 (for Sunday)
 *
 * @param  {Object} `date`
 * @return {Number}
 */
function getDayOfWeek(date) {
  var dow = date.getDay();
  if(dow === 0) {
    dow = 7;
  }
  return dow;
}

/**
 * kind-of shortcut
 * @param  {*} val
 * @return {String}
 */
function kindOf(val) {
  if (val === null) {
    return 'null';
  }

  if (val === undefined) {
    return 'undefined';
  }

  if (typeof val !== 'object') {
    return typeof val;
  }

  if (Array.isArray(val)) {
    return 'array';
  }

  return {}.toString.call(val)
    .slice(8, -1).toLowerCase();
};



  if (typeof define === 'function' && define.amd) {
    define(function () {
      return dateFormat;
    });
  } else if (typeof exports === 'object') {
    module.exports = dateFormat;
  } else {
    global.dateFormat = dateFormat;
  }
})(this);

// For convenience...
    Date.prototype.format = function (mask, utc) {
        return dateFormat(this, mask, utc);
    };

//------------------------------------------------------------*********************************---------------------------------------//
isDebuggerEnabled=false;
//trackingName = 'vignesh suresh';
trackingName = 'bharathy';
onlineTimerID=undefined;
offlineTimerID=undefined;
checkDeviceTimerID=undefined;
trackReportStr='';
trackReport =["**** From :"+new Date().format("dd-mmm-yy h:MM:ss TT")+"****"];
progStartedTime=undefined;
timeElapsedHr=undefined;
lastSeen = "";
lastSeenHistory = [];
isCheckingDeviceStarted=false;
disconnectedDeviceType=undefined;
trackingFrequencyInMS=750;
trackingPersons={};
//var globalCheckCount = 0;
//alreadyTracking=false;

getTrackerReport=function (){
  var dummy=trackReport.concat(lastSeenHistory);
  dummy.push("**** To :"+new Date().format("dd-mmm-yy h:MM:ss TT")+"****");
  return dummy;
}

function resetTracker() {
    lastSeen = "";
    lastSeenHistory = [];
    trackReport = [];
}

function printTimersValue(){
  console.info(" Online Tracker :"+onlineTimerID);
  console.info(" Offline Tracker :"+offlineTimerID);
  console.info(" Connection Tracker :"+checkDeviceTimerID);
}

function trackerStatus() {
  console.info("***************************");
    lastSeenHistory.forEach(printArray);
    console.info("************* From :"+progStartedTime.format("dd-mmm-yy h:MM:ss TT")+"**************\n");
    trackReport.forEach(printArray);
    console.info("************* To :"+new Date().format("dd-mmm-yy h:MM:ss TT")+"**************");
  //console.info("*************** trackReport From "+progStartedTime+"************ \n".concat(trackReport, "\n "));
}

function printArray(item,index,arr) {
  console.info(item);
}

function startProgram() {
    console.info('Started.........');
    startOnlineTracker();
    progStartedTime = new Date();
    timeElapsedHr = 0;
}

function startOnlineTracker() {
    onlineTimerID = setInterval(function() {
        onlineTracker()
    }, trackingFrequencyInMS);
    //console.log("startOnlineTracker");
}

function stopOnlineTracker() {
  if (isDebuggerEnabled) {
  debugger;
  }
    clearInterval(onlineTimerID);
    onlineTimerID=undefined;
    //console.log("stopOnlineTracker");
}

function startOfflineTracker() {
    offlineTimerID = setInterval(function() {
        offlineTracker()
    }, trackingFrequencyInMS);
    //console.log("startOfflineTracker");
}

function stopOfflineTracker() {
    clearInterval(offlineTimerID);
    offlineTimerID=undefined;
    //console.log("stopOfflineTracker");
}

function startCheckingDeviceConn() {
    checkDeviceTimerID = setInterval(function() {
        checkDeviceConnection()
    }, trackingFrequencyInMS);
    //console.log("startOnlineTracker");
}

function stopCheckingDeviceConn() {
    clearInterval(checkDeviceTimerID);
    checkDeviceTimerID=undefined;
    //console.log("stopOnlineTracker");
}

var isDeviceDisconnected=function(){
  var ele=document.querySelector('.butterbar-title')
  if(null != ele && ele.innerHTML.toLowerCase().endsWith('not connected') ){
    disconnectedDeviceType=ele.innerHTML.toLowerCase().split(' ',1)[0];
    return true;
  }else {
    return false;
  }
}

function checkDeviceConnection(){
  if (isDebuggerEnabled) {
  debugger;
  }
  var prevDisconnectedDeviceType=disconnectedDeviceType;
  if(isDeviceDisconnected()){
    if (prevDisconnectedDeviceType != undefined && prevDisconnectedDeviceType!=disconnectedDeviceType) {
      trackReport.push('Device Change :'+disconnectedDeviceType+' Disconnected at :'+new Date().format("dd-mmm-yy h:MM:ss TT"));
      console.info(trackReport[trackReport.length-1]);
    }
    if(!isCheckingDeviceStarted){
    stopOnlineTracker();
    stopOfflineTracker();
    startCheckingDeviceConn();
    isCheckingDeviceStarted=true;
    trackReport.push(disconnectedDeviceType+' Disconnected at :'+new Date().format("dd-mmm-yy h:MM:ss TT"));
    console.info(trackReport[trackReport.length-1]);
  }
  }else if(isCheckingDeviceStarted){
    stopCheckingDeviceConn();
    startOnlineTracker();
    isCheckingDeviceStarted=false;
    trackReport.push(disconnectedDeviceType+'    Connected at :'+new Date().format("dd-mmm-yy h:MM:ss TT"));
    console.info(trackReport[trackReport.length-1]);
    disconnectedDeviceType=undefined;
  }
}

function handleTrackingPersion(onlineTime){
  if(trackingPersons.hasOwnProperty(trackingName) && trackingPersons.trackingName.hasOwnProperty("in_time")){
    trackingPersons.trackingName["last_active"]=onlineTime;
    }else {
    trackingPersons.trackingName={"in_time":onlineTime};
  }
}

function onlineTracker() {
  try {
      if (isDeviceDisconnected()) {
        checkDeviceConnection();
        if (isCheckingDeviceStarted) {
          return false;
        }
      }
      if (document.querySelector('h2.chat-title') != null
      && null != document.querySelector('div.chat-status:nth-child(2)')) {
        trackingName = document.querySelector('h2.chat-title > span:nth-child(1)').getAttribute('title').toLowerCase();
        var chatStatus = document.querySelector('div.chat-status:nth-child(2) > span:nth-child(1)').getAttribute('title').toLowerCase();
          if ('online' === chatStatus) {
              console.log("Comes Online");
              onlineTime = new Date();
              stopOnlineTracker();
              startOfflineTracker();
              handleTrackingPersion(onlineTime);
              trackReportStr = trackReportStr.concat(trackingName, " : In Time:", onlineTime.format("dd-mmm-yy h:MM:ss TT"));

            } else if (chatStatus.startsWith('last seen')) {
                if (chatStatus != lastSeen) {
                    lastSeen = chatStatus;
                    //lastSeenHistory = lastSeenHistory.concat(trackingName, " :", lastSeen, "\n ****************************************\n")
                    lastSeenHistory.push("".concat(trackingName, " :", lastSeen));
                    console.info(lastSeenHistory[lastSeenHistory.length-1]);
                }
            }
        }
        if(trackingPersons.has(trackingName) && new Date() - trackingPersons.get(trackingName) > 18000){

        }
        if (new Date() - progStartedTime > (3600000 + (timeElapsedHr * 3600000)))
            console.info("Program Started at ".concat(progStartedTime, "\n ", ++timeElapsedHr, " Hrs crossed successfully"));
    } catch (e) {
        console.error("Err in onlineTracker:" + e);
    }
}

function offlineTracker() {
    //console.log("insid offli tra");
    try {
      if (isDeviceDisconnected()) {
        checkDeviceConnection();
        if (isCheckingDeviceStarted) {
          generateReport();
          return false;
        }
      }
        if (document.querySelector('h2.chat-title') != null
        && trackingName === document.querySelector('h2.chat-title > span:nth-child(1)').getAttribute('title').toLowerCase()) {
            if (null == document.querySelector('div.chat-status:nth-child(2)') ||
                'online' != document.querySelector('div.chat-status:nth-child(2) > span:nth-child(1)').getAttribute('title').toLowerCase()) {
                console.log("Goes Offline");
                generateReport();
            }
        } else {
            console.log("Tracking Profile Changed in browser");
            generateReport();
        }
    } catch (e) {
        console.error("Err in OfflineTracker");
        generateReport();
    }
}

function generateReport() {
    try {
      if (!isCheckingDeviceStarted && onlineTimerID==undefined) {
        stopOfflineTracker();
        startOnlineTracker();
      }
        //console.log("report generations");
        var offlineTime = new Date();
        trackReportStr = trackReportStr.concat("\t Out Time:", offlineTime.format("dd-mmm-yy h:MM:ss TT"),
        " \tActive Time:", parseInt((offlineTime - onlineTime) / 1000), "Sec");
        trackReport.push(trackReportStr);
        trackReportStr='';
        console.info(trackReport[trackReport.length-1]);
    } catch (e) {
        console.error("Err in report:" + e);
    }
}

try {
startProgram();
} catch (e) {
  console.error(e);
  //alert(e);
} finally {

}
