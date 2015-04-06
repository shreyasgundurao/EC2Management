package com.aws.ec2;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateImageRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public class AWSStarter {

	static AmazonEC2 ec2;

	public static void init() {

		AWSCredentials credentials;
		try {
			credentials = new PropertiesCredentials(
					AWSStarter.class
							.getResourceAsStream("AwsCredentials.properties"));
			System.out.println(credentials.getAWSAccessKeyId());
			System.out.println(credentials.getAWSSecretKey());

			/*********************************************
			 * 
			 * #1 Create Amazon Client object
			 * 
			 * 
			 *********************************************/
			System.out.println("#1 Create Amazon Client object");
			ec2 = new AmazonEC2Client(credentials);
			Region usWest2 = Region.getRegion(Regions.US_WEST_2);
			ec2.setRegion(usWest2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Window.alert("Exceptionnnnnn,,,,.,.");
			System.out.println("Exceptioooommihihni");
			e.printStackTrace();
		}
	}

	public static String launchInstance() throws IllegalArgumentException {

		try {

			// Window.alert("Inside launch instances ..................");
			System.out.println("Inside launch instances ...................");

			init();
			/*
			 * Create an instance
			 */
			RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

			runInstancesRequest.withImageId("ami-70f96e40")
					.withInstanceType("t1.micro").withMinCount(1)
					.withMaxCount(1).withKeyName("sheyaskey")
					.withSecurityGroupIds("sg-ceea1ca1");

			RunInstancesResult runInstancesResult = ec2
					.runInstances(runInstancesRequest);

			/***********
			 * to make sure the instance's state is
			 * "running instead of "pending",
			 **********/
			/*********** we wait for a while **********/
			System.out.println("waiting");
			Thread.currentThread().sleep(5000);
			System.out.println("OK");

			List<Instance> resultInstance = runInstancesResult.getReservation()
					.getInstances();

			String createdInstanceId = null;
			for (Instance ins : resultInstance) {

				createdInstanceId = ins.getInstanceId();
				System.out.println("New instance has been created: "
						+ ins.getInstanceId());// print the instance ID

			}
			return createdInstanceId + " launched";

		} catch (Exception e) {
			System.out.println("launch instance: " + e);
		}
		return null;
	}

	public static String startInstance(String instanceId) {

		init();

		try {
			List<String> instanceIds = new LinkedList<String>();
			instanceIds.add(instanceId);

			// start
			StartInstancesRequest startIR = new StartInstancesRequest(
					instanceIds);
			ec2.startInstances(startIR);

			return instanceId + " started";
		} catch (com.amazonaws.AmazonServiceException e) {
			return "Error! \n" + e.getMessage();
		}

	}

	public static String stopInstance(String instanceId) {

		init();
		try {
			List<String> instanceIds = new LinkedList<String>();
			instanceIds.add(instanceId);

			// stop
			StopInstancesRequest stopIR = new StopInstancesRequest(instanceIds);
			ec2.stopInstances(stopIR);

			return instanceId + " stopped";
		} catch (com.amazonaws.AmazonServiceException e) {
			return "Error! \n" + e.getMessage();
		}
	}

	public static String terminateInstance(String instanceId) {

		init();
		try {
			List<String> instanceIds = new LinkedList<String>();
			instanceIds.add(instanceId);

			// stop
			TerminateInstancesRequest tir = new TerminateInstancesRequest(
					instanceIds);
			ec2.terminateInstances(tir);

			return instanceId + " terminated";
		} catch (com.amazonaws.AmazonServiceException e) {
			return "Error! \n" + e.getMessage();
		}
	}

	public static String rebootInstance(String instanceId) {

		init();
		try {
			List<String> instanceIds = new LinkedList<String>();
			instanceIds.add(instanceId);

			// stop
			RebootInstancesRequest rir = new RebootInstancesRequest(instanceIds);
			ec2.rebootInstances(rir);

			return instanceId + " rebooted";
		} catch (com.amazonaws.AmazonServiceException e) {
			return "Error! \n" + e.getMessage();
		}
	}

	public static String createAMI(String instanceId)
			throws AmazonServiceException {
		try {
			CreateImageRequest image = new CreateImageRequest();
			image.withInstanceId(instanceId);
			image.setName(instanceId);
			ec2.createImage(image);
			return "AMI created successfully";
		} catch (com.amazonaws.AmazonServiceException e) {
			return "Error! \n" + e.getMessage();
		}
	}

	public static String describeInstances() {

		init();
		try {
		System.out.println("#4 Describe Current Instances");
		DescribeInstancesResult describeInstancesRequest = ec2
				.describeInstances();
		List<Reservation> reservations = describeInstancesRequest
				.getReservations();
		Set<Instance> instances = new HashSet<Instance>();
		// add all instances to a Set.
		for (Reservation reservation : reservations) {
			instances.addAll(reservation.getInstances());
		}

		int i = 1;
		System.out.println("Sl.No" + "\t" + "Instance Id" + "\t" + "ImageId"
				+ "\t" + "InstanceType" + "\t" + "State");
		for (Instance ins : instances) {
			// instance id
			String instanceId = ins.getInstanceId();

			// instance state
			InstanceState is = ins.getState();

			String state = is.getName();
			String imageId = ins.getImageId();
			String instanceType = ins.getInstanceType();

			System.out.println(i + ".\t " + ins.getInstanceId() + "\t"
					+ ins.getImageId() + "\t" + ins.getInstanceType() + "\t"
					+ is.getName());
			i++;
		}
		return "success";
		}catch(com.amazonaws.AmazonServiceException e) {
			System.out.println("Empty Key exception" );
			e.printStackTrace();
		}
		
		return null;

	}

	public static void main(String[] args) {

		System.out.println("Please choose from the following: \n"
				+ "1. Launch Instance \n" + "2. Start Instance \n"
				+ "3. Stop Instance \n" + "4. Terminate Instance \n"
				+ "5. Reboot Instance \n" + "6. Create AMIs from Instance \n");
		// init();

		while (true) {
			describeInstances();

			System.out.println("Enter your choice now!");

			Scanner s = new Scanner(System.in);
			int input = s.nextInt();

			switch (input) {

			case 1:
				System.out.println("Launch Instance");
				System.out.println("\n" + launchInstance() + "\n");
				break;

			case 2:
				System.out.println("Start Instance");
				System.out.println("Enter the Instance Id");
				s = new Scanner(System.in);
				String instanceId = s.nextLine();
				System.out.println("\n" + startInstance(instanceId) + "\n");
				break;

			case 3:
				System.out.println("Stop Instance");
				System.out.println("Enter the Instance Id");
				s = new Scanner(System.in);
				instanceId = s.nextLine();
				System.out.println("\n" + stopInstance(instanceId) + "\n");
				break;

			case 4:
				System.out.println("Terminate Instance");
				System.out.println("Enter the Instance Id");
				s = new Scanner(System.in);
				instanceId = s.nextLine();
				System.out.println("\n" + terminateInstance(instanceId) +"\n");
				break;

			case 5:
				System.out.println("Reboot Instance");
				System.out.println("Enter the Instance Id");
				s = new Scanner(System.in);
				instanceId = s.nextLine();
				System.out.println("\n" +rebootInstance(instanceId) + "\n");
				break;

			case 6:
				System.out.println("Create AMI from Instance");
				System.out.println("Enter the Instance Id");
				s = new Scanner(System.in);
				instanceId = s.nextLine();
				System.out.println("\n" +createAMI(instanceId) + "\n");
				break;
				
			default: 
			}
		}
	}

}
