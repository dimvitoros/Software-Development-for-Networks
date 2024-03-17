// global variables in order for the data to be changed

let map;
let markerA;
let markerB;
let markerC;
let previousMarkerA;
let previousMarkerB;
let previousMarkerC;
let rectangle;
let previousRectangle;
let circleA
let previousCircleA
let circleC
let previousCircleC

// initialize the map
async function initMap(){

  map = new google.maps.Map(document.getElementById("map"), {
    center: {lat: 38.0742 , lng: 23.8243},
    zoom: 8
  });

  fetchData();

  setInterval(fetchData, 1000); // Fetch data every 1 second
  
}

// function fetchData to receive the values of iot1, iot2 and android from the server 

function fetchData(){
  fetch('http://localhost:8000/data_endpoint') 
    .then(response => response.json())
    .then(data => {
        console.log('Received data:', data); // Log the received data
        // Update HTML elements with received data
        document.getElementById('Android_id').innerText = data.Android_id;
        document.getElementById('Android_lat').innerText = data.Android_lat;
        document.getElementById('Android_long').innerText = data.Android_long;

        document.getElementById('Iot1_id').innerText = data.Iot1_id;
        document.getElementById('Iot1_smoke').innerText = data.Iot1_smoke;
        document.getElementById('Iot1_gas').innerText = data.Iot1_gas;
        document.getElementById('Iot1_temp').innerText = data.Iot1_temp;
        document.getElementById('Iot1_uv').innerText = data.Iot1_uv;
        document.getElementById('Iot1_lat').innerText = data.Iot1_lat;
        document.getElementById('Iot1_lng').innerText = data.Iot1_lng;
        document.getElementById('Iot1_danger').innerText = data.Iot1_danger;

        document.getElementById('Iot2_id').innerText = data.Iot2_id;
        document.getElementById('Iot2_smoke').innerText = data.Iot2_smoke;
        document.getElementById('Iot2_gas').innerText = data.Iot2_gas;
        document.getElementById('Iot2_temp').innerText = data.Iot2_temp;
        document.getElementById('Iot2_uv').innerText = data.Iot2_uv;
        document.getElementById('Iot2_lat').innerText = data.Iot2_lat;
        document.getElementById('Iot2_lng').innerText = data.Iot2_lng;
        document.getElementById('Iot2_danger').innerText = data.Iot2_danger;
      
        // call the functions made below, with received data from the server

        addAndroidMarker(data.Android_id, data.Android_lat, data.Android_long);
        addIotMarkerB(data.Iot1_id, data.Iot1_smoke, data.Iot1_gas, data.Iot1_temp, data.Iot1_uv, data.Iot1_lat, data.Iot1_lng, data.Iot1_danger);
        addIotMarkerC(data.Iot2_id, data.Iot2_smoke, data.Iot2_gas, data.Iot2_temp, data.Iot2_uv, data.Iot2_lat, data.Iot2_lng, data.Iot2_danger);

        // checks if both iot devices are in danger, in order to draw the rectangle
        
        if(data.Iot1_danger > 0 && data.Iot2_danger > 0){
          drawRectangleAroundIoTDevices(data.Iot1_lat, data.Iot1_lng, data.Iot2_lat, data.Iot2_lng);
        } 
        else if (previousRectangle != null) {         // checks if there is a previous rectangle
          previousRectangle.setMap(null);             // delete it when a new one is constructed
          previousRectangle = null;
        }

    })
    .catch(error => {
        console.error('Error fetching data:', error);
    });
}



// function to draw a rectangle if both iot devices are in danger
function drawRectangleAroundIoTDevices(lat1, lng1, lat2, lng2) {
  if (previousRectangle != null) {
    previousRectangle.setMap(null);
  }

  // Calculate the bounds of the rectangle
  const bounds = new google.maps.LatLngBounds(
    new google.maps.LatLng(Math.min(lat1, lat2), Math.min(lng1, lng2)),
    new google.maps.LatLng(Math.max(lat1, lat2), Math.max(lng1, lng2))
  );

  // Create and draw the rectangle
  rectangle = new google.maps.Rectangle({
    bounds: bounds,
    strokeColor: "#FF0000",
    strokeOpacity: 0.8,
    strokeWeight: 2,
    fillColor: "#FF0000",
    fillOpacity: 0.35,
    map: map
  });

  previousRectangle = rectangle;
}
  // creation of marker for the android device

  function addAndroidMarker(id, lat, lng){


    if (previousMarkerA != null) {        // deletes the marker when a new one is created
      previousMarkerA.setMap(null);
    }


    markerA = new google.maps.Marker({
      position:{lat,lng},
      map: map,
      icon: "android.png",
    });

    const infowindow = new google.maps.InfoWindow({         // infowindow with information o android device
    
      content: "Android Device " + "<br /> " + "<br /> " + 
               "Device id: " + id + "<br /> " + "Latitude: " + lat + "<br /> " +
               "Longtitude: " + lng 
        
    });

    markerA.addListener('click',()=>{     // ability to click on the window and see information of the device
      infowindow.open({
        anchor: markerA,
        map,
      });
    });

    previousMarkerA = markerA;

  }

  // creation of the iot1 device

  function addIotMarkerB(id,smoke, gas, temp, uv, lat, lng, levelofdanger){

    if (previousMarkerB != null) {        // deletes it when a new one is created
      previousMarkerB.setMap(null);
    }


    markerB = new google.maps.Marker({
      position:{lat,lng},
      map: map,
      icon: colorIconForIotDevice(levelofdanger),         // icon set according to the level of danger
    });
    
    if(temp == -1000 && uv != -1000){                     // if temp has no value -> then does not enter temp value in infowindow                                                    
      info = "Iot Device " + "<br /> " + "<br /> " + 
    "Device id: " + id + "<br /> " + "Smoke Sensor: " + smoke + "<br /> " + 
    "Gas Sensor: " + gas + "<br /> " +
    "Ultraviolet Sensor: " + uv + "<br /> " + "Latitude: " + lat + 
    "<br /> " + "Longtitude: " + lng
    }
    else if(uv == -1000 && temp != -1000){            // if uv has no value -> then does not enter uv value in infowindow
      
      info = "Iot Device " + "<br /> " + "<br /> " + 
    "Device id: " + id + "<br /> " + "Smoke Sensor: " + smoke + "<br /> " + 
    "Gas Sensor: " + gas + "<br /> " + "Temperature Sensor: " + temp +
    "<br /> " + "Latitude: " + lat + 
    "<br /> " + "Longtitude: " + lng
    
    }
    else if(uv == -1000 && temp == -1000){            // if temp and uv have no "reasonable" value -> then they are now shown in the infowindow
      info = "Iot Device " + "<br /> " + "<br /> " + 
    "Device id: " + id + "<br /> " + "Smoke Sensor: " + smoke + "<br /> " + 
    "Gas Sensor: " + gas + "<br /> " + "Latitude: " + lat + 
    "<br /> " + "Longtitude: " + lng

    }
    else{                                             // print all the elements in the infowindow
      
      info = "Iot Device " + "<br /> " + "<br /> " + 
    "Device id: " + id + "<br /> " + "Smoke Sensor: " + smoke + "<br /> " + 
    "Gas Sensor: " + gas + "<br /> " + "Temperature Sensor: " + temp +
    "<br /> " + "Ultraviolet Sensor: " + uv + "<br /> " + "Latitude: " + lat + 
    "<br /> " + "Longtitude: " + lng
    
    }
  

    const infowindow = new google.maps.InfoWindow({
    
      content: info
    
  });

  markerB.addListener('click',()=>{     // ability to click in the infowindow
      infowindow.open({
        anchor: markerB,
        map,
      });
  });

  
  let iotActive;

  // if all the devices are active then sets iotActive in 2, so as to create a green circle 
  
  if(levelofdanger != -1){
    iotActive = 2;
  }
  else{
    iotActive = 1;      // if all devices are not active, then sets 1 and creates a red circle 
  }


  if(iotActive == 1){                   // if the devices are not active we draw around the iot a red circle

    if (previousCircleA != null) {
      previousCircleA.setMap(null);
    }

     circleA = new google.maps.Circle({
        strokeColor: "#FF0000",
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: "FF0000",
        fillOpacity: 0.35,
        map,
        center: {lat, lng},
        radius: 15
    });

    previousCircleA = circleA;

  }
  else if(iotActive == 2){                          // if the devices are active we draw around the iot a green circle

    if (previousCircleA != null) {
      previousCircleA.setMap(null);
  }
    circleA = new google.maps.Circle({
        strokeColor: "#00FF00",
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: "00FF00",
        fillOpacity: 0.35,
        map,
        center: {lat, lng},
        radius: 15
    });

    previousCircleA = circleA;
}


function colorIconForIotDevice(levelofdanger){     // choose icon for iot device with "!", depending on the danger level

  let icon;

  if(levelofdanger == 1)
      icon = "orange.png";          // orange icon (!)
  else if(levelofdanger == 2)
      icon = "red.png";          // red icon (!)
  else if(levelofdanger == 0)
      icon = "http://maps.google.com/mapfiles/ms/icons/blue-dot.png";          // simple blue iot marker
  else
      icon = "http://maps.google.com/mapfiles/ms/icons/blue-dot.png";          // simple blue iot marker

  return icon;
  }

  previousMarkerB = markerB;

}
// same function as the previous but with different names, for iot2 device

function addIotMarkerC(id,smoke, gas, temp, uv, lat, lng, levelofdanger){

  if (previousMarkerC != null) {
    previousMarkerC.setMap(null);
  }

  

  markerC = new google.maps.Marker({
    position:{lat,lng},
    map: map,
    icon: colorIconForIotDevice(levelofdanger),
  });

  if(temp == -1000 && uv != -1000){

    info = "Iot Device " + "<br /> " + "<br /> " + 
  "Device id: " + id + "<br /> " + "Smoke Sensor: " + smoke + "<br /> " + 
  "Gas Sensor: " + gas + "<br /> " +
  "Ultraviolet Sensor: " + uv + "<br /> " + "Latitude: " + lat + 
  "<br /> " + "Longtitude: " + lng
  
  }
  else if(uv == -1000 && temp != -1000){
    
    info = "Iot Device " + "<br /> " + "<br /> " + 
  "Device id: " + id + "<br /> " + "Smoke Sensor: " + smoke + "<br /> " + 
  "Gas Sensor: " + gas + "<br /> " + "Temperature Sensor: " + temp +
  "<br /> " + "Latitude: " + lat + 
  "<br /> " + "Longtitude: " + lng
  
  }
  else if(uv == -1000 && temp == -1000){

    info = "Iot Device " + "<br /> " + "<br /> " + 
  "Device id: " + id + "<br /> " + "Smoke Sensor: " + smoke + "<br /> " + 
  "Gas Sensor: " + gas + "<br /> " + "Latitude: " + lat + 
  "<br /> " + "Longtitude: " + lng

  }
  else{
    
    info = "Iot Device " + "<br /> " + "<br /> " + 
  "Device id: " + id + "<br /> " + "Smoke Sensor: " + smoke + "<br /> " + 
  "Gas Sensor: " + gas + "<br /> " + "Temperature Sensor: " + temp +
  "<br /> " + "Ultraviolet Sensor: " + uv + "<br /> " + "Latitude: " + lat + 
  "<br /> " + "Longtitude: " + lng
  
  }

  const infowindow = new google.maps.InfoWindow({
  
    content: info
  
  });

  markerC.addListener('click',()=>{
      infowindow.open({
        anchor: markerC,
        map,
      });
  });



  if(levelofdanger != -1){
    iotActive = 2;
  }
  else{
    iotActive = 1;
  }


  if(iotActive == 1){                                // if the devices are not active we draw around the iot a red circle

    if (previousCircleC != null) {
      previousCircleC.setMap(null);
    }

    circleC = new google.maps.Circle({
      strokeColor: "#FF0000",
      strokeOpacity: 0.8,
      strokeWeight: 2,
      fillColor: "FF0000",
      fillOpacity: 0.35,
      map,
      center: {lat, lng},
      radius: 15
    });

    previousCircleC = circleC;
  }
  else if(iotActive == 2){                          // if the devices are active we draw around the iot a green circle

    if (previousCircleC != null) {
      previousCircleC.setMap(null);
  }

  circleC = new google.maps.Circle({
      strokeColor: "#00FF00",
      strokeOpacity: 0.8,
      strokeWeight: 2,
      fillColor: "00FF00",
      fillOpacity: 0.35,
      map,
      center: {lat, lng},
      radius: 15
  });

  previousCircleC = circleC;
}


function colorIconForIotDevice(levelofdanger){     // choose icon for iot device with "!", depending on the danger level

let icon;

if(levelofdanger == 1)
    icon = "orange.png";          // orange icon (!)
else if(levelofdanger == 2)
    icon = "red.png";          // red icon (!)
else if(levelofdanger == 0)
    icon = "http://maps.google.com/mapfiles/ms/icons/blue-dot.png";          // simple blue iot marker
else
    icon = "http://maps.google.com/mapfiles/ms/icons/blue-dot.png";          // simple blue iot marker

return icon;
}

previousMarkerC = markerC;
   
}



