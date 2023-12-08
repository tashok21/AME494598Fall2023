#define LILYGO_WATCH_2019_WITH_TOUCH
#include <LilyGoWatch.h>
TTGOClass *ttgo;

#include <SimpleDHT.h>

#include <WiFi.h>
#include <HTTPClient.h>


const char* ssid = "Vaishakpixel";
const char* password = "vaishak08";

//Your Domain name with URL path or IP address with path
const char* serverName = "http://34.26.71.245/setValue";

// the following variables are unsigned longs because the time, measured in
// milliseconds, will quickly become a bigger number than can be stored in an int.
//unsigned long lastTime = 0;
// Timer set to 10 minutes (600000)
//unsigned long timerDelay = 600000;
// Set timer to 5 seconds (5000
unsigned long current=0;
unsigned long previous=0;
unsigned long timerDelay = 5000;
unsigned long requeststart=0;
unsigned long requeststop=0;
unsigned long totaltime;

String response;

// for DHT11,
//      VCC: 5V or 3V
//      GND: GND
//      DATA: 21 or 25
int pinDHT11 = 25;
SimpleDHT11 dht11(pinDHT11);
const int MAX_DATA_POINTS = 100; 
float tempHistory[MAX_DATA_POINTS];
float humHistory[MAX_DATA_POINTS];
int dataIndex = 0;


String httpGETRequest(const char* serverName) {
  HTTPClient http;
    
  // Your IP address with path or Domain name with URL path 
  http.begin(serverName);
  
  // Send HTTP POST request
  int httpResponseCode = http.GET();
  
  String payload = "{}"; 
  
  if (httpResponseCode>0) {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    payload = http.getString();
  }
  else {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }
  // Free resources
  http.end();

  return payload;
}


void displayHistory() {
    // Example: Display last 10 readings
    int displayCount = min(10, dataIndex);
    float tempSum = 0;
    float humSum = 0;

    for (int i = 0; i < displayCount; i++) {
        int index = (dataIndex - 1 - i + MAX_DATA_POINTS) % MAX_DATA_POINTS;
        ttgo->tft->drawString("Temp: " + String(tempHistory[index]) + " C",  0, i * 16);
        ttgo->tft->drawString("Hum: " + String(humHistory[index]) + " %",  100, i * 16);
        tempSum += tempHistory[index];
        humSum += humHistory[index];
    }

    float avgTemp = tempSum / displayCount;
    float avgHum = humSum / displayCount;
    ttgo->tft->drawString("Avg Temp: " + String(avgTemp) + " C", 0, 160);
    ttgo->tft->drawString("Avg Hum: " + String(avgHum) + " %", 100, 160);
}
}
void setup() {
  current=millis();
  previous=millis();
  requeststart=millis();
  requeststop=millis();
  totaltime=0;

  Serial.begin(115200);
    ttgo = TTGOClass::getWatch();
    ttgo->begin();
    ttgo->openBL();
    
    ttgo->tft->fillScreen(TFT_BLACK);
    ttgo->tft->setTextColor(TFT_WHITE, TFT_BLACK);
    ttgo->tft->setTextFont(4);

    
      WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
 
  Serial.println("Timer set to 5 seconds (timerDelay variable), it will take 5 seconds before publishing the first reading.");

void storeData(float temperature, float humidity) {
    tempHistory[dataIndex] = temperature;
    humHistory[dataIndex] = humidity;
    dataIndex = (dataIndex + 1) % MAX_DATA_POINTS; // loop back when reaching array end
}


void loop() {
  current=millis();
  if((totaltime+current-previous)>timerDelay){
  
  // start working...
  Serial.println("=================================");
  Serial.println("Sample DHT11...");
 

  // read without samples.
  byte temperature = 0;
  byte humidity = 0;
  int err = SimpleDHTErrSuccess;
  if ((err = dht11.read(&temperature, &humidity, NULL)) != SimpleDHTErrSuccess) {
    Serial.print("Read DHT11 failed, err="); Serial.println(err); delay(1000);
    return;
  }
  
  Serial.print("Sample OK: ");
  Serial.print(String((float)temperature) + "* C, ");
  Serial.println(String((float)humidity) + "% H");

      ttgo->tft->drawString(String((int)temperature*1.8 + 32) + " *F",  5, 10);
      ttgo->tft->drawString(String(humidity) + " % H",  5, 40);

      int t = (int)temperature;
      int h = (int)humidity;
      String url = String(serverName) + "?t=" + t + "&h=" + h;
      Serial.println(url);    
      requeststart=millis();
      response = httpGETRequest(url.c_str());
      requeststop=millis();
      totaltime=(requeststop-requeststart);
      Serial.println(response);
      previous=millis();

  }

  // DHT11 sampling rate is 1HZ.
  //delay(500);
}
