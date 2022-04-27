 import java.util.*;
 import java.io.*;
 import java.net.*;
 
 import java.nio.charset.StandardCharsets;   
 
 
 /*******************************  THE  CLASS TO SEND DATA BACK TO CLIENT ON A SEPARATE THREAD ***********************/
  class PacketReceiverThread implements Runnable{
	
	private DatagramSocket ds;
	private DatagramPacket dpSend;
	private SD_DataPacket packet;
	

	public PacketReceiverThread(SD_DataPacket dp, InetAddress host, int port) throws SocketException{
		
		this.ds=new DatagramSocket(0);
		
		this.dpSend=new DatagramPacket(new byte[2048],2048,host,port);
		
		this.packet=dp;
		
	}
	
	public void run(){
		try{
			//set its acknowledgement 
			this.packet.setAckState(true);
			
			//set from Server
			this.packet.setFromServer(true);
			
			//wait randomly so that some packets are acknowledged not on first come first basis but randomly - for sliding window - for testing only
			//Thread.sleep((long)Math.random()*1000);
			
			//set the data 
			this.dpSend.setData(this.packet.getNetworkFormat());
			
			
			//send
			this.ds.send(dpSend);
			System.out.println( "\n*************************************************\n");
			System.out.println( "\nAcknowledged Packet No ....." + this.packet.getSN() + "\n data is : " + this.packet.getData());
			System.out.println( "*************************************************\n");
			
			
		}catch(Exception e){
			System.out.println("\nFailed sending acknowledgement for Packet No: " + this.packet.getSN());
		}
		
		
	}
	
}

/******************************* END THE  CLASS TO SEND DATA BACK TO CLIENT ON A SEPARATE THREAD ***********************/

 /*******************************  THE SERVER CLASS ***********************/
 public class Server{
	 
	 public static void main(String args[])
	 {
		 Scanner scn=new Scanner(System.in);
		 
		 //for simulating loss of packet No. 2, No5 - each 3 times - for sliding window - for testing only
		 int packetNo2=0,packetNo5=0;
		 
		 //for tracking the expected packet no
		 //public volatile int expectedPacketNo=1;
		
		try{
		
				
				//Get details like port number and host
				System.out.println("Enter port number to bind to :");
				int port=scn.nextInt();
				
				//create the DatagramSocket
				DatagramSocket ds=new DatagramSocket(port);
				System.out.println("\nDatagram socket bound on port  : " + port);

				//create a datagramPacket to receive 
				DatagramPacket dpReceive =new DatagramPacket(new byte[2048], 2048);
				
				boolean cont=true;
				 
				 // listen for connections 
				 while(cont==true){
					 
						 
						 //get packets 
						 ds.receive(dpReceive);
						 
						 
						 //get the data 
						SD_DataPacket packet=new SD_DataPacket();
						
						//get the data 
						byte[] byteData=dpReceive.getData();
						
						//recover the data
						packet.retriveNetworkFormat(byteData);
						
						//String fullData=new String(byteData,StandardCharsets.UTF_8);
						//testing 
						//System.out.println("\nData is  : "  + fullData);
						//System.out.println(packet.getPacketNo() + " "  +packet.getData());
						
						
						//simulate loss of certain packets  - Packets 2 and 5 - each for 3 times - for sliding window - for testing only
						if(packet.getSN()==2 && packetNo2<3){
							System.out.println("\nReceived Packet No 2 for the occurance no : "  +  (packetNo2+1) );
							System.out.println( "\nSimulating loss....." +  "\n--------------------------------------------\n");
							packetNo2++;
							continue;
						}
						
						if(packet.getSN()==5 && packetNo5<3){
							System.out.println("\nReceived Packet No 5 for the occurance no : "  +  (packetNo5+1) );
							System.out.println( "\nSimulating loss....." +  "\n--------------------------------------------\n");
							packetNo5++;
							continue;
						}
						
						//acknowledge packets  on anoter thread
						Runnable prt =new PacketReceiverThread(packet, dpReceive.getAddress(), dpReceive.getPort());
						Thread tAck=new Thread(prt);
						
						tAck.start();
						
						
						
						 
				 }
				 
				 System.out.println("\n ********************** SERVER STOPPED *********************************");
				 
		}catch(SocketException se){
			System.out.println("The port is not free. Please run the program again and choose a different port");
		}
		catch(Exception e){
			System.out.println("Server terminated:" +  e);
		}
		
	 }
 }
 
 /*******************************  END OF THE SERVER CLASS ***********************/