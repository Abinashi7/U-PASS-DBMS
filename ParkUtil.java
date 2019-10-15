import java.sql.*;
import java.util.Collections;



public class ParkUtil {
    /**
     * WALLET_ID  |GUEST_ID   |FAMILY_NAME                   |GIVEN_NAME                    |TOTAL_NUM_&|VISIT_DATE|GUEST_ID   |UPASS_ID   |ENTRY_T&|ATTRACTION&|ATTRACTION&|ATTRACTION_NAME               |RUNN&|PASSES_AVA&|PASSES_STO&
     * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * 1          |1          |doe                           |john                          |4          |2019-06-26|1          |1          |13:30:00|1          |1          |fun                           |true |9          |10
     * 2          |2          |sabirova                      |lilya                         |4          |2019-06-26|2          |2          |10:30:00|2          |2          |hooray                        |true |9          |10
     * 3          |3          |singh                         |abi                           |4          |2019-06-26|3          |3          |11:40:00|3          |3          |jumbo                         |true |9          |10
     */
    /**
     * Print complete guest info.
     * @param conn the connection
     * @throws SQLException if a database operation fails
     */
static void completeGuestInfo(Connection conn) throws SQLException{
    try (
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select * from ((Guest join Wallet on Guest.guest_id = Wallet.guest_id) join (Upass join Attraction on Upass.attraction_id= Attraction.attraction_id ) USING (wallet_id))");
    ) {
        System.out.println("\nGuest Info:");
        while (rs.next()) {
            int guest_id = rs.getInt(2);
            String familyName = rs.getString(3);
            String givenName = rs.getString(4);
            int wallet_id= rs.getInt(1);
            int passes= rs.getInt(5);
            Date date= rs.getDate(6);
            Time entry_time= rs.getTime(9);
            String attraction_name= rs.getString(12);

            System.out.printf("  %s %s (%d) | wallet_id: (%d) wallet_perHour_capacity: (%d) purchaseDate: [%s] |  Entry time: (%s) attraction_name: %s\n", familyName, givenName, guest_id, wallet_id,passes, date, entry_time, attraction_name);
        }
    }

}

    /**
     * Print guest and corresponding wallet.
     * @param conn the connection
     * @throws SQLException if a database operation fails
     */

static void printCorrespondingwallets(Connection conn)throws SQLException{
    try (
            Statement stmt = conn.createStatement();
            // list of present Guest and their id's
            ResultSet rs = stmt.executeQuery(
                    "select * from Guest join Wallet on Guest.guest_id = Wallet.guest_id");
    ) {
        System.out.println("\nCurrent guests and corresponding Wallets:");
        while (rs.next()) {
            int guest_id = rs.getInt(1);
            String familyName = rs.getString(2);
            String givenName = rs.getString(3);
            int wallet_id= rs.getInt(4);
            int passes= rs.getInt(5);
            Date date= rs.getDate(6);
            int guest_id_wallet= rs.getInt(7);
            System.out.printf("  %s, %s (%d) (wallet_id: %d), wallet_perHour_capacity: %d, purchaseDate: %s, corresponding_guest_id: %d\n", familyName, givenName, guest_id, wallet_id,passes, date, guest_id_wallet);
        }
    }

}

    /**
     * Print guest table.
     * @param conn the connection
     * @return number of guests at present
     * @throws SQLException if a database operation fails
     */
    static int printGuests(Connection conn) throws SQLException {
        try (
                Statement stmt = conn.createStatement();
                // list of present Guest and their id's
                ResultSet rs = stmt.executeQuery(
                        "select guest_id, family_name, given_name from Guest order by family_name, given_name");
        ) {
            System.out.println("\nCurrent guests:");
            int count = 0;
            while (rs.next()) {
                int guest_id = rs.getInt(1);
                String familyName = rs.getString(2);
                String givenName = rs.getString(3);
                System.out.printf("  %s, %s (%d)\n", familyName, givenName, guest_id);
                count++;
            }
            return count;
        }
    }

    /**
     * Print Wallet table.
     * @param conn the connection
     * @return number of Wallet at present
     * @throws SQLException if a database operation fails
     */
    static int printWallets(Connection conn) throws SQLException {
        try (
                Statement stmt = conn.createStatement();
                // list of present wallets and their issue dates
                ResultSet rs = stmt.executeQuery(
                        "select wallet_id, total_num_UP, visit_date from Wallet order by visit_date");
        ) {
            System.out.println("\nActive Wallets:");
            int count = 0;
            while (rs.next()) {
                int wallet_id = rs.getInt(1);
                int total_num_UP = rs.getInt(2);
                Date visit_date = rs.getDate(3);
                System.out.printf("  Issue Date:%s (%d)\n", visit_date, wallet_id);
                count++;
            }
            return count;
        }
    }

    /**
     * Print Attraction table.
     * @param conn the connection
     * @return number of Attractions at present
     * @throws SQLException if a database operation fails
     */
    static int printAttractions(Connection conn) throws SQLException {
        try (
                Statement stmt = conn.createStatement();
                // list of present Guest and their id's
                ResultSet rs = stmt.executeQuery(
                        "select attraction_id, attraction_name, running, passes_available from Attraction order by attraction_name");
        ) {
            System.out.println("\nCurrent Attractions:");
            int count = 0;
            while (rs.next()) {
                int attraction_id = rs.getInt(1);
                String Name = rs.getString(2);
                int passes_available= rs.getInt(4);
                Boolean status = rs.getBoolean(3);
                System.out.printf("  Name -> %s, Status ->%b, Passes_available ->%d (%d)\n", Name, status,passes_available, attraction_id);
                count++;
            }
            return count;
        }
    }

    /**
     * Print Upass table.
     * @param conn the connection
     * @return number of Upass at present
     * @throws SQLException if a database operation fails
     */
    static int printUpass(Connection conn) throws SQLException {
        try (
                Statement stmt = conn.createStatement();
                // list of present Guest and their id's
                ResultSet rs = stmt.executeQuery(
                        "select upass_id, entry_time from Upass order by entry_time");
        ) {
            System.out.println("\nCurrent U-passes:");
            int count = 0;
            while (rs.next()) {
                int upass_id = rs.getInt(1);
                Time entry_time = rs.getTime(2);
                System.out.printf(" entry_time: %s (%d)\n", entry_time, upass_id);
                count++;
            }
            return count;
        }
    }



}
