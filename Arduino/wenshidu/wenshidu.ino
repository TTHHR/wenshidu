#include <SoftwareSerial.h>
#include <ESP8266WiFi.h>
#include <WiFiUdp.h>

 String ssid     = "PandoraBox";//这里是我的wifi，你使用时修改为你要连接的wifi ssid
 String password = "tianhaoran";//你要连接的wifi密码
const char *host = "wjy.qingyuyu.cn";//服务端的IP地址
const String token="";//作为身份识别，这里已经删去

SoftwareSerial mySerial(4, 5); // RX, TX 软串口，用来连接蓝牙
WiFiUDP  Udp;
const int  remPort = 1234;//远程端口，这里是UDP端口，TCP走2333端口
boolean ledStatus=false;
int times=0;
float temp=0l,humi=0l;//储存温湿度的变量
void setup() {
  pinMode(LED_BUILTIN, OUTPUT);     // Initialize the LED_BUILTIN pin as an output
  Serial.begin(9600);
  mySerial.begin(38400);
  Serial.println("boot up");

linkWifi();//先连接一次WIFI
   
}

// the loop function runs over and over again forever
void loop() {
if (WiFi.status() != WL_CONNECTED)//WIFI断开
{
ledBlink();//使led闪烁
}
else
{
  if(times>=100)//delay(50)*100=5s 发送一次数据
  {
       getTempAndHumi();//先从设备获取数据，根据你自己的传感器
      Serial.println("sent to server....");//提示信息
      String data="{\"need\":\"set\",\"token\":\""+token+"\",\"data\":\"temp="
      +temp+",humi="+humi
      +"\"}\n";//组建数据
      
      char buff[500];
      data.toCharArray(buff,data.length()+1);
      Udp.beginPacket(host,remPort);
      Udp.write(buff);
      Udp.endPacket();
      
     Serial.println("ok");
     times=0;
  }
}
if (mySerial.available()) {//蓝牙有数据传入
 String s= mySerial.readStringUntil('\n');//数据以换行结尾
    Serial.println(s);
    doCheakCmd(s);//检查和运行命令
  }
 delay(50);
 times++;
}
void linkWifi()
{
  char s[50],p[50];
  ssid.toCharArray(s,ssid.length()+1);
  password.toCharArray(p,password.length()+1);
  Serial.println("ssid:"+String(s));
  Serial.println("password:"+String(p));
WiFi.begin(s, p);//开始连接WIFI
int cnt=0;
    while (WiFi.status() != WL_CONNECTED&&cnt<20)//WiFi.status() ，这个函数是wifi连接状态，返回wifi链接状态
                                         //这里就不一一赘述它返回的数据了，有兴趣的到ESP8266WiFi.cpp中查看
    {
       ledBlink();//未连接期间，闪烁LED
        Serial.print(".");
        cnt++;
    }
 if(cnt<20)//如果有连上wifi
{
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());//WiFi.localIP()返回8266获得的ip地址
    digitalWrite(LED_BUILTIN,LOW); //wifi指示灯打开
    Serial.println("Working...");
 }
}
void ledBlink()
{
 digitalWrite(LED_BUILTIN,(ledStatus=!ledStatus)?HIGH:LOW);
 delay(500);
}
void doCheakCmd(String message)
{
  short n=message.indexOf(',');//数据以,分割
if(message.substring(0,n).equals(token))//第一条是token
{
  
  message=message.substring(n+1,message.length());
  Serial.println(message);
  n=message.indexOf(',');
  if(n!=-1)
  ssid=message.substring(0,n);//第二条是ssid
  else
  return;
 
  password=message.substring(n+1,message.length());//第三条是密码
 

  linkWifi();//连接wifi
}
else
Serial.println("token error");
return ;
}
void getTempAndHumi()
{
	//这里看具体传感器修改
temp=27.1;
humi=20.4;
}
