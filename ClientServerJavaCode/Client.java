 import java.util.*;
 import java.io.*;
 import java.net.*;
 import java.sql.Timestamp;
 
 import java.nio.charset.StandardCharsets;  
 import java.nio.file.Path;
 import java.nio.file.Paths;
 import java.nio.file.Files;

/******************************* THE CLASS FOR REPRESENTING THE DATA PACKET ***********************/

class SD_DataPacket{
	 
	 /* Random string to act as separator of different parts in payload */
	 private String partSeparator = "#@";
	 
	 
	 private int SN; 
	 private int totalPackets;
	 private String data;
	 
	 /*
		fromServer for marking whether it is from server or client
		true- from server
		false - from client 
	 
	 */
	 private boolean fromServer;
	 
	 /*
		for setting acknowledgement of receipt of packet
	 */
	 
	 private boolean ackState;
	 
	 
	 /* for setting the sliding door */
	 private int doorSize ;
	 
	 /* for setting the current timestamp */
	 private long timestamp;
	 
	 /* Constructor */
	 public SD_DataPacket(){
		 this.SN=-1;
		 this.data="not set";
		 this.fromServer=false;
		 this.ackState=false; 
		 this.doorSize=-1;
		 this.timestamp=-1;
	 }
	 
	 public SD_DataPacket(int SN, String data, boolean fromServer, boolean ackState, int doorSize,long timestamp){
		 this.SN=SN;
		 this.data=data;
		 this.fromServer=fromServer;
		 this.ackState=ackState; 
		 this.doorSize=doorSize;
		 this.timestamp=timestamp;
	 }
	 
	 
	 /*getters and setters*/
	 public void setSN(int SN){
		 this.SN=SN;
	 }
	 
	 public int getSN(){
		 return this.SN;
	 }
	 
	 public void setTotalPackets(int totalPackets){
		 this.totalPackets=totalPackets;
	 }
	 
	 public int getTotalPackets(){
		 return this.totalPackets;
	 }
	 
	 public void setData(String data){
		 this.data=data;
	 }
	 
	 public String getData(){
		 return this.data;
	 }
	 
	 public void setFromServer(boolean fromServer){
		 this.fromServer=fromServer;
	 }
	 
	 public boolean getFromServer(){
		return this.fromServer;
	 }
	 
	 public void setAckState(boolean ackState){
		this.ackState=ackState;
	 }
	 
	 public boolean getAckState(){
		 return this.ackState;
	 }
	 
	 
	 public void setDoorSize(int doorSize){
		 this.doorSize=doorSize;
	 }
	 
	 public int getDoorSize(){
		 return this.doorSize;
	 }

	public void setTimestamp(long timestamp){
		this.timestamp=timestamp;
	}
	 
	 public long getTimestamp(){
		return this.timestamp;
	 }
	 
	 /*helper methods*/
	 
	 /*convert to byte array */
	 public byte[] getNetworkFormat(){
		 
		 String fullData="SN:"+ SN + partSeparator + "Data:" + data + partSeparator + "FromServer:" + fromServer + partSeparator + "ACK:" +  ackState;
		 fullData += partSeparator + "Timestamp:" + timestamp;
		 return fullData.getBytes();
		 
	 }
	 
	 /*get back data from byte array or check that data is valid */
	 public boolean retriveNetworkFormat(byte byteData[]){
		 
		 String fullData=new String(byteData,StandardCharsets.UTF_8);
		 String [] dataParts= fullData.split(partSeparator);
		 
		 //testing 
		 //System.out.println("\nTotal parts : " + dataParts.length);
		 
		 /*check format is correct */
		 if(dataParts.length !=5)
			 return false;
		 
		 if(!dataParts[0].startsWith("SN:"))
			 return false;
		 
		 if(!dataParts[1].startsWith("Data:"))
			 return false;
		 
		 if(!dataParts[2].startsWith("FromServer:"))
			 return false;
		 
		 if(!dataParts[3].startsWith("ACK:"))
			 return false;
		 
		 /*retrieve data*/
		 String dataValue;
		 
		 for(int i=0;i<dataParts.length;i++)
		 {
			 dataValue=dataParts[i].split(":")[1];
			 
			 switch(i)
			 {
				 case 0:
					try{
						this.SN=Integer.parseInt(dataValue);
					}
					catch(Exception nfe){
						return false;
					}
					break;
				 case 1:
					this.data=dataValue;
					break;				 
				 case 2:
					try{
						this.fromServer=Boolean.parseBoolean(dataValue);
					}
					catch(Exception e){
						return false;
					}
					
						
					break;				 
				 case 3:
					try{
						this.ackState=Boolean.parseBoolean(dataValue);
					}
					catch(Exception e){
						return false;
					}
					
			 }
		 }
		 
		 return true;
	 }
	 
 }
 
/******************************* END OF THE CLASS FOR REPRESENTING THE DATA PACKET ***********************/




/******************************* THE CLASS FOR HELPING  DATA PACKET IN A COLLECTION ***********************/
 
   class SD_ClientPacketHelper{
	 
	 private Map<Integer,SD_DataPacket> packetCollection;
	 
	 public SD_ClientPacketHelper(){
		 packetCollection=new HashMap<Integer,SD_DataPacket>();
	 }
	 
	 /*initializing to 20 packets */
	 public void initializePackets(int size,String dataToSend){
		
			//if we are reinitializing the same object used previously
			packetCollection.clear();
			
			//get the timestamp of current date
			 //Timestamp timestampNow = new Timestamp(System.currentTimeMillis());
			
			for(int i=0;i<size;i++){
				SD_DataPacket obj=new 	SD_DataPacket();
				
				/*set the properties*/
				
				
				obj.setSN(i+1);
				
				//set directly or get data from a file
				obj.setData(dataToSend + ": " + (i+1));
				
				obj.setFromServer(false);
				obj.setAckState(false); 
				
				
				//add to hashmap
				packetCollection.put((i+1),obj);
		 		
			}
			
	 }
	 
	 public void removePacket(int SN){
		 packetCollection.remove(SN);
	 }
	 
	 public int getNoOfPackets(){
		 return packetCollection.size();
	 }
	 
	 public int getNoOfPacketsNotAck(){
		 
		 int total=(int)packetCollection
						.entrySet()
						.stream()
						.filter(mapObj-> mapObj.getValue().getAckState()==false)
						.count();
						
		return total;
		 
	 }
	 
	 
	 public boolean markAck(SD_DataPacket sdp){
		 try{
			int  key=sdp.getSN();
			
			packetCollection.replace(key,sdp);
			return true;
		 
		 }catch(Exception e){
			 return false;
		 }
			
	 }
	 
	 public boolean isPacketAck(int SN){
		 return packetCollection.get(SN).getAckState();
	 }
	 
	 
	 public SD_DataPacket getPacket(int SN){
		 return packetCollection.get(SN);
	 }
	 
	 /*public  List<SD_DataPacket> getPacketsNotReceived(){
		 
		 
		 
	 }*/
	 
	 
	 
 }
 
 
/******************************* END OF THE CLASS FOR HELPING  DATA PACKET IN A COLLECTION ***********************/
 
 
 /*******************************  THE CLIENT CLASS ***********************/
 public class Client{
	 
	 //Datagram socket to send and receive- we will bind to random port
	 private DatagramSocket ds=null;
	 
	 //to get file name from which data should be sent 
	 private String fileInputName;
	 private String dataToSend;
	 
	 //serverPort- for port of server, windowSize- for window size, noOfPackets - total packets desired to be sent to server, 
	 //timeoutInMills- time to wait for response from server before resending packets of windowSize
	 private int serverPort,windowSize,noOfPackets,timeoutInMills;
	 
	 //helper object to send data to server
	 private SD_ClientPacketHelper clientHelper=null;
	 
	 //create a dqueue to track the sliding window
	 private Deque deque=null;
	 
	 //track the last packet acknowledged 	- 0 means none because packet number begins from 1
	 int  lastAck;
	 
	 //InetAddress of server- we will assume localhost i.e., 127.0.0.1
	 InetAddress serverAddress=null; 
	 
	 //DatagramPacket to send data to server
	 DatagramPacket dpSend=null;
	 
	 //to temporarily hold data for check ACK and updating 
	 SD_DataPacket packetTemp=null;
	 
	 
	 /* method for initializing the properties */
	 private void initialize(){
		 try{
		
					Scanner scn=new Scanner(System.in);
				
					//initialize temporary packet 
					packetTemp=new SD_DataPacket();
					
					
					//bind to port
					ds=new DatagramSocket(0);
					System.out.println("\nDatagram socket bound on port  : " + ds.getPort());
					
					//get server port
					System.out.println("\nEnter the port of the server:");
					serverPort=scn.nextInt();
					
					//get window size
					System.out.println("\nEnter the window size:");
					windowSize=scn.nextInt();
					
					
					//get total number of packets
					System.out.println("\nEnter total number of packets:");
					noOfPackets=scn.nextInt();
					
					
					//get wait time before resending in seconds
					System.out.println("\nEnter total wait time before resending(in seconds):");
					timeoutInMills=  scn.nextInt() ;
					//convert to milliseconds
					timeoutInMills*=1000;
					//set to socket
					ds.setSoTimeout(timeoutInMills);
					
					
					//get the name of the file, which contains the data to be sent 
					System.out.println("\nEnter the name of the input file(assuming same DIR):");
					fileInputName="input.txt";
					
					//get a path object 
					Path path = Paths.get(fileInputName);
					dataToSend = Files.readAllLines(path).get(0);
					

					//create given number of packets
					clientHelper=new SD_ClientPacketHelper();
					clientHelper.initializePackets(noOfPackets,dataToSend);
					
					//create a dqueue to track the sliding window
					deque = new LinkedList();
					
					//put the number of packets to be sent in deque
					for(int i=1;i<=windowSize;i++){
						deque.addLast(i);
					}
						
					//track the last packet acknowledged 	- 0 means none because packet number begins from 1
					lastAck=0;
					
					
					//assuming that the server is on the localhost- otherwise, we have to input and create InetAddress
					serverAddress = InetAddress.getByName("127.0.0.1");
					
					
					
					//create the packet 
					dpSend =new DatagramPacket(new byte[2048], 2048,serverAddress,serverPort);
					
			
			 }catch(SocketException se){
					System.out.println("The port is not free. Please run the program again and choose a different port");
			}
			 catch(Exception e){
				 System.out.println("\nApplication terminated : " + e);
			 }
		
		 
	 }
	 
	 
	 
	 //logic to send data of window size in current window
	 private void sendCurrentWindow() throws IOException{
		 
		 //copy to list to preserve original deque 
		 List list = (LinkedList)(deque);
		 
		 //create same timestamp for marking all within same window - for future innovation - just a thought :-)
		 Timestamp timestamp = new Timestamp(System.currentTimeMillis());	 		 
		 int index=0;
	 
		 for(int i=((int)deque.peekFirst());i<=((int)deque.peekLast());i++){
			//get the packet 
			SD_DataPacket sd_dp=clientHelper.getPacket((int)list.get(index));
			
			//set same timestamp for all in same window
			sd_dp.setTimestamp(timestamp.getTime());
			 
			//set its data and send it  
			dpSend.setData(sd_dp.getNetworkFormat());
			ds.send(dpSend);
			
			index++;
						
		 }
					
	 }
	 
	 
	 
	 
	 
	 //main logic to send and implement GoBack(n) with Sliding Window
	 private  void runClient() throws IOException{
		 
		 //DatagramPacket to receive
		 DatagramPacket dpReceive =new DatagramPacket(new byte[2048], 2048);
			
				
				//initially send sliding window size 
				sendCurrentWindow();
				
				//for loop continuation
				boolean cont=true;
			
				//keep repeating till exception occurs
				while(cont==true){		
					
					
					
					try{
						
						//wait for incoming data 
						ds.receive(dpReceive);
						
						//get the data into readable format of our custom packet
						packetTemp.retriveNetworkFormat(dpReceive.getData());
						
						//get ACK 
						boolean isAcknowledged=packetTemp.getAckState();
						
						//get Packet No
						int currPacketNo=packetTemp.getSN();
						
						System.out.println("\n ************ PACKET RECEIVED " + currPacketNo + " ****************************");
						
						//if packet is acknowledged and the number of the packet number acknowledged is in the sequence expected
						if(isAcknowledged==true && currPacketNo==(lastAck+1)){
							//update acknowledged
							lastAck++;
							
							System.out.println("\n ************ PACKET RECEIVED " + currPacketNo + "  is ACKNOWLEDGED AND EXPECTED NO. "+ (lastAck) + " ********");
							//System.out.println("\n ************ Total Packets  " + noOfPackets +" ****************************");
							
							if(lastAck>= noOfPackets){
								System.out.println("\n ************ Last packet received -------- TERMINATING CLIENT  *******");
								break;
							}
							
							//update collection 
							clientHelper.markAck(packetTemp);
							
							
							//update deque - remove first and add at last
							int nextFrame=((int)deque.peekLast()) + 1;
							deque.removeFirst();

							
							if(nextFrame <= noOfPackets){
								deque.addLast(nextFrame);
							}
							
							 //create same timestamp for marking the frame - for future innovation - just a thought :-)
							Timestamp timestamp = new Timestamp(System.currentTimeMillis());	 		 
		 
							//send last frame  and continue
							SD_DataPacket sd_dp=clientHelper.getPacket((int)deque.peekLast());
			
							//set same timestamp for all in same window
							sd_dp.setTimestamp(timestamp.getTime());
							 
							//set its data and send it  
							dpSend.setData(sd_dp.getNetworkFormat());
							ds.send(dpSend);
								
							
						}
						else{
							//since last desired packet No was skipped repeat window 
							System.out.println("\n ************ PACKET RECEIVED " + currPacketNo + "  is ACKNOWLEDGED AND EXPECTED NO. was "+ (lastAck+1)  + " ********");
							System.out.println("\n ************  ##########   RESENDING CURRENT WINDOW    ##########  ********");
							sendCurrentWindow();
						}
						
						
					}catch(SocketTimeoutException  ste){
						
						//since timeout has occured without any reciving of data - resend the current window 
						sendCurrentWindow();
						
					}
				}
					
	 			//if acknowledgement has skipped packet go back n to last ack + 1 packet and resend
				
			 
			 
	 }
	 
	 
	 //main method
	 public static void main(String args[])
	 {
		 try{
				 Client obj=new Client();
				 
				 //initialize
				 obj.initialize();
				 
				 //run
				 obj.runClient(); 
				 
		 }catch(Exception e){
			 
			 System.out.println("Program terminated due to error: " + e);
			 
		 }
	 }
 }
 
 
 /******************************* END THE CLIENT CLASS ***********************/