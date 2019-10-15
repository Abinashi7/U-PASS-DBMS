

import java.sql.*;
import java.util.Properties;
import java.sql.Date;
import java.sql.Time;

public class TestPark {

    public static void main(String[] args) {

        try {
            AmusementPark.main(args); //Loading DDL
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // the default framework is embedded
        String protocol = "jdbc:derby:";
        String dbName = "Parkdata";
        String connStr = protocol + dbName+ ";create=true";

        // tables tested by this program
        String dbTables[] = {
                "Guest", "Wallet", "Upass", "Attraction"
        };


        Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derbyclient frameworks
        props.put("user", "user1");
        props.put("password", "user1");

        // result set for queries
        ResultSet rs = null;
        try (
                // connect to database
                Connection  conn = DriverManager.getConnection(connStr, props);
                Statement stmt = conn.createStatement();

                // insert prepared statements
                PreparedStatement insertRow_guest = conn.prepareStatement(
                        "insert into Guest (family_name, given_name) values(?, ?)",Statement.RETURN_GENERATED_KEYS);

                PreparedStatement insertRow_wallet = conn.prepareStatement(
                        "insert into Wallet(total_num_UP, visit_date, guest_id) values(?, ?, ?)",Statement.RETURN_GENERATED_KEYS);

                PreparedStatement insertRow_upass = conn.prepareStatement(
                        "insert into Upass(entry_time, attraction_id, wallet_id) values(?, ?, ?)",Statement.RETURN_GENERATED_KEYS);

                PreparedStatement insertRow_Attraction = conn.prepareStatement(
                        "insert into Attraction(attraction_name, running, passes_available, passes_stored) values(?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);

                PreparedStatement insertRow_Attraction2 = conn.prepareStatement(
                        "UPDATE Attraction set Attraction.running= ? where attraction_id= ?");


        ) {
            // connect to the database using URL
            System.out.println("Connected to database " + dbName);

            // clear data from tables
            for (String tbl : dbTables) {
                try {
                    stmt.executeUpdate("delete from " + tbl);
                    System.out.println("Truncated table " + tbl);
                } catch (SQLException ex) {
                    System.out.println("Did not truncate table " + tbl);
                }
            }

            //Populating ATTRACTION

            System.out.println("\n");
            String AttractionName[]= {"fun","hooray","jumbo"};
            Boolean RunningStatus[]= {true,true,true};
            int passesAvailable[]= {10,10,10};
            int passesStored[]= {10,10,10};
            int Attraction_id[]= new int[passesStored.length];


            for (int i=0; i<3; i++) {
                try {
                    insertRow_Attraction.setString(1, AttractionName[i]);
                    insertRow_Attraction.setBoolean(2, RunningStatus[i]);
                    insertRow_Attraction.setInt(3, passesAvailable[i]);
                    insertRow_Attraction.setInt(4, passesStored[i]);
                    insertRow_Attraction.execute();
                    rs= insertRow_Attraction.getGeneratedKeys();
                    if( rs!=null && rs.next()) {
                        Attraction_id[i]= rs.getInt(1);
                    System.out.printf("Inserted Attraction: Atrraction_id: %d Name: %s Running: %b PassesAvailable: %d \n",
                            rs.getInt(1),AttractionName[i], RunningStatus[i], passesAvailable[i]);
                     }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }


            // GUEST
            System.out.println("\n");

            String familyName[] = { "doe", "sabirova", "singh" };
            String givenName[] = { "john", "lilya", "abi" };
            int guest_id[]= new int[familyName.length];

            // add GUEST if does not exist
            for(int i=0; i < familyName.length; i++){
                try {

                    insertRow_guest.setString(1, familyName[i]);
                    insertRow_guest.setString(2, givenName[i]);
                    insertRow_guest.execute();
                    rs= insertRow_guest.getGeneratedKeys();
                    if( rs!=null && rs.next()) {
                        guest_id[i]=rs.getInt(1);
                    System.out.printf("Inserted guest: %s %s with Id: %d \n",
                            givenName[i], familyName[i], rs.getInt(1));
                     }
                } catch (SQLException ex) {
                    System.err.printf("\nDid not insert guest: first name: %s last name: %s  Exception message: %s\n", familyName[i], givenName[i], ex.getMessage());
                }
            }

            //WALLET
            System.out.println("\n");


            // current date format = 2015-03-26
            String date[]= {"2019-06-26","2019-06-26","2019-06-26"}; // PLEASE CHANGE IT TO CURRENT DATE. trigger will prevent you from adding a wallet
           int total_passes_perHour[]= {4,4,4};
             int wallet_id[]= new int[date.length];


            for(int i=0; i< date.length; i++) {
                try {
                    insertRow_wallet.setInt(1, total_passes_perHour[i]);
                    insertRow_wallet.setDate(2, Date.valueOf(date[i]));
                    insertRow_wallet.setInt(3, guest_id[i]);
                    insertRow_wallet.execute();
                    rs= insertRow_wallet.getGeneratedKeys();
                    if( rs!=null && rs.next()) {
                        wallet_id[i]=rs.getInt(1);
                        System.out.printf("Inserted Wallet: wallet_id: %d with limit of passes per Hour: %d on Date: %s \n",
                                rs.getInt(1), total_passes_perHour[i], date[i]);
                    }
                } catch (SQLException ex ) {
                    System.out.println(ex.getMessage());
                }
            }
/**
 * CASE-> Adding wallet with an expiry date. This should fire the trigger and in turn won't allow U-passes to be added.
 * */

//            String Expirydate[]= {"2018-06-25","2018-06-25","2018-06-25"};
//            for(int i=0; i< Expirydate.length; i++) {
//                try {
//                    PreparedStatement insertRow_wallet1 = conn.prepareStatement(
//                            "insert into Wallet(total_num_UP, visit_date, guest_id) values(?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
//                    insertRow_wallet1.setInt(1, total_passes_perHour[i]);
//                    insertRow_wallet1.setDate(2, Date.valueOf(Expirydate[i]));
//                    insertRow_wallet1.setInt(3,1);
//                    insertRow_wallet1.execute();
//                    rs= insertRow_wallet1.getGeneratedKeys();
//
//                    if( rs!=null && rs.next()) {
//                        System.out.printf("Inserted Wallet: wallet_id:%d with limit of:%d passes Per Hour: on Date: %s \n",
//                                rs.getInt(1), total_passes_perHour[i], Expirydate[i]);
//                    }
//                } catch (SQLException ex ) {
//                    System.err.printf(" unable to enter this Wallet_id: %d, %s\n", wallet_id[i],ex.getMessage());
//                }
//            }

            //UPASS

            System.out.println("\n");
            Time time = new Time(00-00-00);

            Time entryTime[]={time.valueOf("13:30:00"), time.valueOf("10:30:00"), time.valueOf("11:40:00")};


            for(int i=0; i< entryTime.length; i++) {
                try {
                    insertRow_upass.setTime(1, entryTime[i]);
                    insertRow_upass.setInt(2,Attraction_id[i]);
                    insertRow_upass.setInt(3,wallet_id[i]);
                    insertRow_upass.execute();
                    rs= insertRow_upass.getGeneratedKeys();

                    if( rs!=null && rs.next()) {
                        System.out.printf("Inserted Upass: Upass_id: %d Issue_time: %s\n",
                                rs.getInt(1), entryTime[i]);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                    System.err.printf("unable to enter Upass: upass_id: message: %s \n", ex.getMessage());
                }
            }


/** CASE-> where a guest is coming after hours: working hours are between 8AM to 10PM
 **/

//            Time entryTimeInvalif[]={time.valueOf("23:30:00"), time.valueOf("10:30:00"), time.valueOf("1:40:00")};
//
//            for(int i=0; i< entryTimeInvalif.length; i++) {
//                try {
//                    PreparedStatement insertRow_upass1 = conn.prepareStatement(
//                            "insert into Upass(entry_time, attraction_id, wallet_id) values(?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
//                    insertRow_upass1.setTime(1, entryTimeInvalif[i]);
//                    insertRow_upass1.setInt(2,Attraction_id[i]);
//                    insertRow_upass1.setInt(3,wallet_id[i]);
//                    insertRow_upass1.execute();
//                    rs= insertRow_upass1.getGeneratedKeys();
//                    while (rs!=null && rs.next()) {
//                        System.out.printf("Inserted Upass: Upass_id:%d Issue_time:%s\n",
//                                rs.getInt(1), entryTime[i]);
//                    }
//                } catch (SQLException ex) {
//
//                        System.err.printf("unable to enter Upass:  message: %s \n",  ex.getMessage());
//
//                }
//            }


/** CASE-> selling more than per hour capacity(which is 4). Adding 4 new passes at the same time. Should not enter more than 4 in this case.
 **/

//            for (int i = 0; i < 4 ; i++) {
//
//                try {
//                    PreparedStatement insertRow_upass2 = conn.prepareStatement(
//                            "insert into Upass(entry_time, attraction_id, wallet_id) values(?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
//
//                    insertRow_upass2.setTime(1, entryTime[1]);
//                    insertRow_upass2.setInt(2, Attraction_id[2]);
//                    insertRow_upass2.setInt(3, wallet_id[0]);
//                    insertRow_upass2.execute();
//                    rs=insertRow_upass2.getGeneratedKeys();
//
//                    while(rs!=null && rs.next()) {
//
//                        System.out.printf("Inserted Upass: Upass_id:%d Issue_time:%s\n",
//                                rs.getInt(1), entryTime[1]);
//                    }
//                } catch (SQLException ex) {
//                    System.err.printf("\nunable to purchase Upass: Message:%s \n", ex.getMessage());
//                }
//            }

/** CASE-> setting an attraction to false. All the U-pass Availability related to the attraction will be cancelled and customers will get notification
 **/
            int countGuests=0; int countAtt=0;  int countUpass=0; int countWallets=0;

//            stmt.executeUpdate("update Attraction set running='false' where attraction_id= 1");



/** CASE-> setting the same attraction to work back again. The passes available should turn back to it's original capacity(10), and no Upass has been sold related to it.
 **/

//            stmt.execute("update Attraction set running = true where attraction_id = 1");


/** CASE->trying to add more U-passes than a wallet's capacity(4).
 **/

//            Time entryTime1[]={time.valueOf("10:0:00"), time.valueOf("10:30:00"), time.valueOf("10:40:00"),time.valueOf("12:30:00"),time.valueOf("12:35:00"),time.valueOf("12:30:00"),time.valueOf("12:39:00")};
//
//            for (int i = 0; i < 4; i++) {
//
//                try {
//                    PreparedStatement insertRow_upass3 = conn.prepareStatement(
//                            "insert into Upass(entry_time, attraction_id, wallet_id) values(?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
//                    insertRow_upass3.setTime(1, entryTime1[i]);
//                    insertRow_upass3.setInt(2, Attraction_id[2]);
//                    insertRow_upass3.setInt(3, wallet_id[1]);
//                    insertRow_upass3.execute();
//                    rs= insertRow_upass3.getGeneratedKeys();
//
//                    while (rs!=null && rs.next()) {
//                        System.out.printf("Inserted Upass: Upass_id:%d Issue_time:%s\n",
//                                rs.getInt(1), entryTime1[i]);
//                    }
//                  } catch (SQLException ex) {
//                    System.err.printf("\nunable to purchase Upass: Message: %s \n", ex.getMessage());
//                }
//            }

/** CASE-> Deleting all the guests. resulting count of Guest, Wallet and U-passes should be 0.
 **/

//            stmt.execute("DELETE from Guest where given_name='john'");
//            stmt.execute("DELETE from Guest where given_name='lilya'");
//            stmt.execute("DELETE from Guest where given_name='abi'");

            /** PRINTING FULL GUEST INFO */
            ParkUtil.completeGuestInfo(conn);

/**
 *  -> test
 */
            countGuests=ParkUtil.printGuests(conn);
            System.out.printf("count:%d\n\n",countGuests);
            countAtt=ParkUtil.printAttractions(conn);
            System.out.printf("count:%d\n\n",countAtt);
            countUpass=ParkUtil.printUpass(conn);
            System.out.printf("count:%d\n\n",countUpass);
            countWallets=ParkUtil.printWallets(conn);
            System.out.printf("count:%d\n\n",countWallets);
            // print number of rows in tables
            for (String tbl : dbTables) {
                rs = stmt.executeQuery("select count(*) from " + tbl);
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.printf("Table %s : count: %d\n", tbl, count);
                }
            }
           rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}