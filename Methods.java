//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.sql.Time;

public class Methods {

    /**
     * This helper function is used to enforce the limit of passes sold to a single wallet
     * @throws Exception if the max amount of passes limit is exceeded
     */
    public static void WalletCap() throws Exception {

        throw new Exception("U-Pass cannot be added. Wallet reached it's max capacity for the day!");
    }

    /**
     * This helper function is used to enforce the limit of passes sold per hour for a single attraction
     * @throws Exception if the max amount of passes per hour limit is exceeded
     */
    public static void walletPerHour(int wallet_id, java.sql.Time time) throws Exception{

        long future2 = time.getTime()+3600000;
        Time con= new Time(future2);
        System.out.printf("\nyour wallet: %d has a max per hour capacity, please try and purchase after: %s\n", wallet_id, con);
        throw new Exception("\nThis Upass cannot be purchase at this moment.\n");

    }

    /**
     * This helper function is used to prevent an expired wallet from being used
     * @throws Exception if the Wallet has expired
     */
    public static void walletExpired() throws Exception{

        throw new Exception("You are seeing this message because your wallet has been expired. Please Purchase a new Wallet\n");
    }

    /**
     * // CURRENTLY NOT IN USE- WILL USE WHILE BUILDING APP
     * This helper function notifies guests in the park when a certain attraction is out-of-order
     * @param attraction: the attraction which is currently out-of-order
     */
    public static void AttractionDownMes(String attraction) {
        System.out.printf("\n\nYour ride (%s) has been cancelled because it is out-of-order. Refund will be sent immediately. Apologies for the inconvinience\n\n",attraction);
    }

    /**
     * // CURRENTLY NOT IN USE- WILL USE WHILE BUILDING APP
     * This helper function notifies guests in the park when a certain attraction which was out-of-order is back again.
     * @param attraction: the attraction name.
     */
    public static void AttractionUpMes(String attraction) {
        System.out.printf("\n\nYay! dear visitors, ride (%s) is back for fun. Please visit ticket-booth to buy tickets\n\n",attraction.toString());
    }

    /**
     * This helper function is used to enforce operating hours of the park
     * @throws Exception if the Upass hours are not within the operating hours of the park
     */
    public static void BusinessHours()throws Exception {
        throw new Exception("Operating hours are between 8am and 10 pm. We look forward to seeing you soon!");
    }


}
