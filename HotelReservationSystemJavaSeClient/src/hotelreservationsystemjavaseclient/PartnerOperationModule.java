/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelreservationsystemjavaseclient;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import ws.client.CreateNewReservationException_Exception;
import ws.client.InputDataValidationException_Exception;
import ws.client.InsufficientRoomsAvailableException_Exception;
import ws.client.LocalDateTime;
import ws.client.PartnerEntity;
import ws.client.PartnerEntityWebService;
import ws.client.PartnerNotFoundException_Exception;
import ws.client.ReservationEntity;
import ws.client.UnknownPersistenceException_Exception;

/**
 *
 * @author mingy
 */
public class PartnerOperationModule {

    private PartnerEntityWebService webServicePort;

    private PartnerEntity currentPartner;

    public PartnerOperationModule() {
    }

    public PartnerOperationModule(PartnerEntityWebService webServicePort, PartnerEntity currentPartner) {
        this.webServicePort = webServicePort;
        this.currentPartner = currentPartner;
    }

    public void menuGuestOperation() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Hotel Reservation System Reservation Partner System :: Partner Operation ***\n");
            System.out.println("1: Partner Reserve Hotel Room");
            System.out.println("2: View Partner Reservation Details");
            System.out.println("3: View All Partner Reservations");
            System.out.println("4: Exit");
            response = 0;

            while (response < 1 || response > 4) {
                response = scanner.nextInt();

                if (response == 1) {
                    doSearchHotelRoom();
                } else if (response == 2) {
                    try {
                        doViewPartnerReservationDetails();
                    } catch (PartnerNotFoundException_Exception ex) {
                        System.out.println("Partner account does not exist");
                    }
                } else if (response == 3) {
                    doViewAllPartnerReservation();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    private void doSearchHotelRoom() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Hotel Reservation System Reservation Client System :: Partner Operation :: Search Hotel Room ***\n");

        java.time.LocalDateTime reservationStartDate;
        int startDay;
        int startMonth;
        int startYear;
        while (true) {
            System.out.println("");
            System.out.println("Please Enter Reservation Start Date> ");
            System.out.println("------------------------");
            System.out.println("Enter Day>     (please select from 01 - 31)");
            startDay = scanner.nextInt();
            System.out.println("Enter Month>   (please select from 01 - 12)");
            startMonth = scanner.nextInt();
            System.out.println("Enter Year>    (please select from 2000 - 2999)");
            startYear = scanner.nextInt();
            try {
                reservationStartDate = java.time.LocalDateTime.of(startYear, startMonth, startDay, 0, 0, 0);
            } catch (DateTimeException ex) {
                System.out.println("DATE INVALID! PLEASE KEY IN APPROPRIATE DATE");
                System.out.println(":::");
                System.out.println("");

                continue;
            }
            break;
        }

        java.time.LocalDateTime reservationEndDate;
        int endDay;
        int endMonth;
        int endYear;
        while (true) {
            System.out.println("");
            System.out.println("Please Enter Reservation End Date> ");
            System.out.println("------------------------");
            System.out.println("Enter Day>     (please select from 01 - 31)");
            endDay = scanner.nextInt();
            System.out.println("Enter Month>   (please select from 01 - 12)");
            endMonth = scanner.nextInt();
            System.out.println("Enter Year>    (please select from 2000 - 2999)");
            endYear = scanner.nextInt();
            try {
                reservationEndDate = java.time.LocalDateTime.of(endYear, endMonth, endDay, 0, 0, 0);
            } catch (DateTimeException ex) {
                System.out.println("DATE INVALID! PLEASE KEY IN APPROPRIATE DATE");
                System.out.println(":::");
                System.out.println("");

                continue;
            }
            break;
        }
        int noRooms = 0;
        while (true) {
            noRooms = 0;
            System.out.println("Please select number of rooms to book>");
            noRooms = scanner.nextInt();
            if (noRooms > 0) {
                break;
            } else {
                System.out.println("Please select a valid number above 0!");
            }
        }
        scanner.nextLine();
        System.out.println("Please wait while we retrieve the available room types...");
        List<String> list;
        try {
            list = webServicePort.retrieveRoomTypeAvailabilities(startDay, startMonth, startYear, endDay, endMonth, endYear, noRooms, false);
            HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
            for (String line : list) {
                String[] arr = line.split(",");
                String roomTypeName = arr[0];
                Integer numRoomType = Integer.parseInt(arr[1]);
                Integer bestPrice = Integer.parseInt(arr[2]);
                HashMap<String, Integer> stringToIntegerMap = new HashMap<>();
                stringToIntegerMap.put("numRoomType", numRoomType);
                stringToIntegerMap.put("bestPrice", bestPrice);
                map.put(roomTypeName, stringToIntegerMap);
            }
            List<String> listOfKeys = new ArrayList<>(map.keySet());

            while (true) {
                System.out.println("");
                System.out.println("------------------------");
                System.out.println("Available Rooms to book from " + reservationStartDate.toLocalDate().toString() + " to " + reservationEndDate.toLocalDate().toString());
                System.out.printf("%5.5s%20.20s%20.20s%20.20s\n", "S/N", "Room Type", "Total Price of Stay", "Quantity Available");
                int counter = 1;
                for (String roomType : listOfKeys) {
                    HashMap<String, Integer> roomTypeMap = map.get(roomType);
                    if (roomTypeMap.get("numRoomType") > 0) {
                        System.out.printf("%5d%20.20s%20.20s%20.20s\n", counter, roomType, roomTypeMap.get("bestPrice"), roomTypeMap.get("numRoomType"));
                        counter += 1;
                    }
                }
                System.out.println("------------------------");
                Integer response = 0;
                System.out.println("1: Reserve room/s (Walk-In)");
                System.out.println("2: Back\n");
                while (response < 1 || response > 2) {
                    response = scanner.nextInt();
                    if (response == 1) {
                        doHotelReserve(map, reservationStartDate, reservationEndDate, noRooms);
                    } else if (response == 2) {
                        break;
                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }
                break;
            }

            System.out.println("");

        } catch (InsufficientRoomsAvailableException_Exception ex) {
            System.out.println("Insufficient rooms are available from " + reservationStartDate.toLocalDate().toString() + " to " + reservationEndDate.toLocalDate().toString());
        }
    }

    private void doHotelReserve(HashMap<String, HashMap<String, Integer>> map, java.time.LocalDateTime reservationStartDate, java.time.LocalDateTime reservationEndDate, Integer numRooms) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** Hotel Management Client :: Front Office Module :: Partner Reserve ***\n");
        System.out.println("");
        List<ReservationEntity> listOfNewReservation = new ArrayList<>();
        List<String> listOfNewReservationsStringOfRoomRateNames = new ArrayList<>();
        BigDecimal totalPayment = BigDecimal.ZERO;

        List<String> listOfKeys = new ArrayList<>(map.keySet());
        int numReservation = 1;
        while (numRooms >= numReservation) {
            ReservationEntity newReservation = new ReservationEntity();

            String firstName = "";
            String lastName = "";
            String email = "";
            String contactNumber = "";
            String passportNumber = "";

            while (true) {
                System.out.println("Enter Partner Guest's First Name>");
                firstName = scanner.nextLine();
                if (firstName.length() > 0) {
                    break;
                } else {
                    System.out.println("Please input a First Name");
                }
            }

            while (true) {
                System.out.println("Enter Partner Guest's Last Name>");
                lastName = scanner.nextLine();
                if (lastName.length() > 0) {
                    break;
                } else {
                    System.out.println("Please input a Last Name");
                }
            }

            //unsure how to check if email is valid at the client side
            System.out.println("Enter Partner Guest's Email>");
            email = scanner.nextLine();

            while (true) {
                System.out.println("Enter Partner Guest's Contact Number>");
                contactNumber = scanner.nextLine();
                if (contactNumber.length() == 8) {
                    break;
                } else {
                    System.out.println("Please input a valid Contact Number");
                }
            }

            while (true) {
                System.out.println("Enter Partner Guest's Passport Number>");
                passportNumber = scanner.nextLine();
                if (passportNumber.length() == 8) {
                    break;
                } else {
                    System.out.println("Please input a valid Passport Number");
                }
            }

            System.out.println("");
            System.out.println("------------------------");
            System.out.println("Available Rooms to book from " + reservationStartDate.toLocalDate().toString() + " to " + reservationEndDate.toLocalDate().toString());
            System.out.printf("%5.5s%20.20s%20.20s%20.20s\n", "S/N", "Room Type", "Total Price of Stay", "Quantity Available");
            List<String> roomTypeNameList = new ArrayList<>();
            int counter = 1;
            for (String roomType : listOfKeys) {
                HashMap<String, Integer> roomTypeMap = map.get(roomType);
                if (roomTypeMap.get("numRoomType") > 0) {
                    System.out.printf("%5d%20.20s%20.20s%20.20s\n", counter, roomType, roomTypeMap.get("bestPrice"), roomTypeMap.get("numRoomType"));
                    counter += 1;
                    roomTypeNameList.add(roomType);
                }
            }

            System.out.println("------------------------");
            System.out.println("Please select room type for reservation number: " + numReservation);
            Integer response = 0;
            String selectedRoomType = null;
            String stringOfRoomRateNames = "";
            while (response < 1 || response > roomTypeNameList.size()) {
                response = 0;
                response = scanner.nextInt();
                if (response >= 1 && response <= roomTypeNameList.size()) {
                    selectedRoomType = roomTypeNameList.get(response - 1);
                    HashMap<String, Integer> selectedRoomTypeMap = map.get(selectedRoomType);
                    List<String> listOfRoomRateNames = new ArrayList<>(selectedRoomTypeMap.keySet());
                    listOfRoomRateNames.remove("bestPrice");
                    listOfRoomRateNames.remove("numRoomType");
                    for (String roomRateName : listOfRoomRateNames) {
                        stringOfRoomRateNames += roomRateName + ",";
                    }
                    newReservation.setRoomTypeName(selectedRoomType);
                    newReservation.setReservationPrice(BigDecimal.valueOf(map.get(selectedRoomType).get("bestPrice")));
                    totalPayment = totalPayment.add(newReservation.getReservationPrice());
                } else {
                    System.out.println("Invalid option, please try again!\n");
                    continue;
                }
            }

            listOfNewReservation.add(newReservation);
            listOfNewReservationsStringOfRoomRateNames.add(stringOfRoomRateNames);

            HashMap<String, Integer> stringToBigDecimalMap = map.get(selectedRoomType);
            stringToBigDecimalMap.put("numRoomType", stringToBigDecimalMap.get("numRoomType") - 1);

            numReservation += 1;
        }

        try {
            
            webServicePort.createNewReservationsForUser(listOfNewReservation, listOfNewReservationsStringOfRoomRateNames, currentPartner);
            System.out.println("::::::::::::::::::::::::::::::::::::::::::");
            System.out.println("Reservations are successful!");
            System.out.println("");
            java.time.LocalDateTime currDateTime = java.time.LocalDateTime.now();
            java.time.LocalDateTime currDate2Am = java.time.LocalDateTime.of(LocalDate.now(), LocalTime.of(2, 0));

            //after 2am walk in, if reservations are for TODAY, then immediately allcoate
            if (currDateTime.isAfter(currDate2Am) && reservationStartDate.isEqual(java.time.LocalDateTime.of(LocalDate.now(), LocalTime.MIN))) {
                webServicePort.allocationReportCheckTimerManual();
            }
        } catch (UnknownPersistenceException_Exception | CreateNewReservationException_Exception ex) {
            System.out.println("Unable to create Reservations, Please Try Again!");
        } catch (InputDataValidationException_Exception ex) {
            System.out.println("Some details are invalid, Please Try Again!");
        }
    }

    private void doViewPartnerReservationDetails() throws PartnerNotFoundException_Exception {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("*** Hotel Reservation System Reservation Partner System :: Partner Operation :: View Partner Reservation Details***\n");
            List<ReservationEntity> partnerReservations = webServicePort.retrieveAllPartnerReservations(currentPartner.getUserEntityId());

            Integer option = 0;
            while (true) {
                System.out.println("----------------------------------------");
                for (int i = 0; i < partnerReservations.size(); i++) {
                    System.out.println((i + 1) + " Reservation Id: " + partnerReservations.get(i).getReservationEntityId());
                }
                option = 0;
                while (option < 1 || option > partnerReservations.size()) {
                    System.out.println("----------------------------------------");
                    System.out.println("Enter Reservation Number Option");
                    option = scanner.nextInt();

                    if (option >= 1 && option <= partnerReservations.size()) {
                        ReservationEntity reservation = partnerReservations.get(option - 1);
                        Long reservationId = reservation.getReservationEntityId();
                        LocalDateTime reservationStartDate = reservation.getReservationStartDate();
                        LocalDateTime reservationEndDate = reservation.getReservationEndDate();
                        String reservationFirstName = reservation.getFirstName();
                        String reservationLastname = reservation.getLastName();
                        String reservationEmail = reservation.getEmail();
                        String reservationContactNumber = reservation.getContactNumber();
                        String reservationPassportNumber = reservation.getPassportNumber();
                        System.out.println("Reservation successfully retrieved. Reservation Id: " + reservationId);
                        System.out.println("Reservation First Name: " + reservationFirstName);
                        System.out.println("Reservation Last Name: " + reservationLastname);
                        System.out.println("Reservation Start Date: " + reservationStartDate.toString());
                        System.out.println("Reservation End Date: " + reservationEndDate.toString());
                        System.out.println("Reservation Email: " + reservationEmail);
                        System.out.println("Reservation Contact Number: " + reservationContactNumber);
                        System.out.println("Reservation Passport Number: " + reservationPassportNumber);

                        System.out.print("Press any key to continue...> ");
                        scanner.nextLine();
                    } else {
                        System.out.println("Please select a valid input!");
                        System.out.println("");
                    }
                }
                System.out.println("");
                System.out.println("Continue to view Reservation? Press 'Y', to Exit Press any other key...");
                String response = scanner.nextLine();
                if (response == "Y") {
                    continue;
                }
                break;
            }
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println("Partner account does not exist");
        }

    }

    private void doViewAllPartnerReservation() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("*** Hotel Reservation System Reservation Partner System :: Partner Operation :: View All Parnter Reservations ***\n");

            List<ReservationEntity> partnerReservations = webServicePort.retrieveAllPartnerReservations(currentPartner.getUserEntityId());

            System.out.printf("%15.15s%15.15s%15.15s%15.15s%15.15s%15.15s%15.15s\n", "Reservation Start Date", "Reservation End Date", "First Name", "Last Name", "Email", "Contact Number", "Passport Number");
            for (ReservationEntity reservationEntity : partnerReservations) {
                System.out.printf("%15.15s%15.15s%15.15s%15.15s%15.15s%15.15s%15.15s\n", reservationEntity.getReservationStartDate().toString(), reservationEntity.getReservationEndDate().toString(), reservationEntity.getFirstName(), reservationEntity.getLastName(), reservationEntity.getEmail(), reservationEntity.getContactNumber(), reservationEntity.getPassportNumber());
            }
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println("Partner does not exist");
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();

    }
}
