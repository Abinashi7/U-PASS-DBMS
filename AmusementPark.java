import
		java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class AmusementPark {

	public static void main(String[] args) throws SQLException {
		// the default framework is embedded
		String protocol = "jdbc:derby:";
		String dbName = "Parkdata";
		String connStr = protocol + dbName+ ";create=true";

//     // tables created by this program
		String dbTables[] = {
				"Upass", "Wallet", "Guest", "Attraction"      // entities
		};

		// triggers created by this program
		String dbTriggers[] = {
				"AttractionDown",
				"AttractionDown2",
				"AttractionDownMessage",
				"AttractionUp",
				"AttractionUpMessage",
				"ExpiredWallet",
				"ExceedsTotal",
				"ExceedsMaxPerHour",
				"attrPassAvail",
				"attrPassAvail2",
				"BusinessHours",
				"BusinessHours2"
		};

		// STORED PROCEDURES
		String dbFunctions[] = {
				"BusinessHours",
				"sendAttractionDownMessage",
				"sendAttractionUpMessage",
				"walletperHour",
				"WalletCap",
				"WalletExp"
		};

		Properties props = new Properties(); // connection properties
		// providing a user name and password is optional in the embedded
		// and derbyclient frameworks
		props.put("user", "user1");
		props.put("password", "user1");

		try (
				// connect to the database using URL
				Connection conn = DriverManager.getConnection(connStr, props);

				// statement is channel for sending commands thru connection
				Statement stmt = conn.createStatement();
		){
			System.out.println("Connected to and created database " + dbName);

			// drop the database triggers and recreate them below
			for (String tgr : dbTriggers) {
				try {
					stmt.executeUpdate("drop trigger " + tgr);
					System.out.println("Dropped trigger " + tgr);
				} catch (SQLException ex) {
					//ex.printStackTrace();
					System.out.println("Did not drop trigger " + tgr);
				}
			}

			// drop the database tables and recreate them below
			for (String tbl : dbTables) {
				try {
					stmt.executeUpdate("drop table " + tbl);
					System.out.println("Dropped table " + tbl);
				} catch (SQLException ex) {
					//ex.printStackTrace();
					System.out.println("Did not drop table " + tbl);
				}
			}

			// drop the database functions and recreate them below
			for (String tbl : dbFunctions) {
				try {
					stmt.executeUpdate("drop procedure " + tbl);
					System.out.println("Dropped function " + tbl);
				} catch (SQLException ex) {
					//ex.printStackTrace();
					System.out.println("Did not drop function " + tbl);
				}
			}


			//CREATE Attraction
			String createTable_Attraction=
					"create table Attraction ("
							+ "attraction_id  INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
							+ "attraction_name varchar(30),"
							+ "running Boolean not null default true,"
							+ "passes_available int not null,"
							+ "passes_stored int not null,"
							+ "Primary key (attraction_id)"
							+")";
			stmt.executeUpdate(createTable_Attraction);
			System.out.println("Created table Attraction");


			//create guest
			String createTable_Guest=
					"create table Guest("
							+ "guest_id  INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
							+ "family_name varchar(30),"
							+ "given_name varchar(30),"
							+ "Primary key(guest_id)"
							+ ")";
			stmt.executeUpdate(createTable_Guest);
			System.out.println("Created table Guest");

			//create wallet
			// Wallet per hour per pass capacity = 4
			// wallet capacity = 4 (added as a constant in the Expired wallet trigger)
			String createTable_Wallet=
					"create table Wallet ("
							+ "wallet_id  INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
							+ "total_num_UP int not null,"
							+ "visit_date date,"
							+ "guest_id int,"
							+ "Primary key(wallet_id),"
							+ "Foreign key(guest_id) references Guest (guest_id) ON DELETE CASCADE"
							+ ")";
			stmt.executeUpdate(createTable_Wallet);
			System.out.println("Created table Wallet");

			//create Upass
			String createTable_Upass=
					"create table Upass ("
							+ "upass_id  INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
							+ "entry_time time,"
							+ "attraction_id int,"
							+ "wallet_id int,"
							+ "Primary key (upass_id),"
							+ "Foreign key (attraction_id) references Attraction (attraction_id) on delete cascade,"
							+ "Foreign key (wallet_id) references Wallet (wallet_id) ON DELETE CASCADE"
							+ ")";
			stmt.executeUpdate(createTable_Upass);
			System.out.println("Created table Upass");


			//STORED FUNCTIONS/PROCEDURES

			// prints exception message
			String createFunction_BusinessHours =
					"CREATE PROCEDURE BusinessHours("
							+ ")"
							+ " PARAMETER STYLE JAVA"
							+ " LANGUAGE JAVA"
							+ " DETERMINISTIC"
							+ " NO SQL"
							+ " EXTERNAL NAME"
							+"        'Methods.BusinessHours'";

			stmt.executeUpdate(createFunction_BusinessHours);
			System.out.println("Created stored procedure BusinessHours");

			// CURRENTLY NOT IN USE- WILL USE WHILE BUILDING APP
			String createProcedure_sendAttractionDownMessage =
					"CREATE PROCEDURE sendAttractionDownMessage("
							+ "		IN attraction_name varchar(30)"
							+ "	)"
							+ " PARAMETER STYLE JAVA"
							+ " LANGUAGE JAVA"
							+ " DETERMINISTIC"
							+ " NO SQL"
							+ " EXTERNAL NAME"
							+ "		'Methods.AttractionDownMes'";
			stmt.executeUpdate(createProcedure_sendAttractionDownMessage);
			System.out.println("Created stored procedure send notification upon attraction being down");

			// CURRENTLY NOT IN USE- WILL USE WHILE BUILDING APP
			String createProcedure_sendAttractionUpMessage =
					"CREATE PROCEDURE sendAttractionUpMessage("
							+ "		IN attraction_name varchar(30)"
							+ "	)"
							+ " PARAMETER STYLE JAVA"
							+ " LANGUAGE JAVA"
							+ " DETERMINISTIC"
							+ " NO SQL"
							+ " EXTERNAL NAME"
							+ "		'Methods.AttractionUpMes'";
			stmt.executeUpdate(createProcedure_sendAttractionUpMessage);
			System.out.println("Created stored procedure send notification upon attraction is working");


			// prints exception message
			String createFunction_walletPerHourLimit =
					"CREATE PROCEDURE walletPerHour("
							+ "	  IN wallet_id int,"
							+ "	  IN entry_time time"
							+ ")"
							+ " PARAMETER STYLE JAVA"
							+ " LANGUAGE JAVA"
							+ " DETERMINISTIC"
							+ " NO SQL"
							+ " EXTERNAL NAME"
							+"        'Methods.walletPerHour'";

			stmt.executeUpdate(createFunction_walletPerHourLimit);
			System.out.println("Created stored function walletPerHourLimit");

			// prints exception message
			String createFunction_walletCap =
					"CREATE PROCEDURE WalletCap("
							+ ")"
							+ " PARAMETER STYLE JAVA"
							+ " LANGUAGE JAVA"
							+ " DETERMINISTIC"
							+ " NO SQL"
							+ " EXTERNAL NAME"
							+"        'Methods.WalletCap'";

			stmt.executeUpdate(createFunction_walletCap);
			System.out.println("Created stored function to check on Wallet Capacity");

			// prints exception message
			String createFunction_walletExpired =
					"CREATE PROCEDURE WalletExp("
							+ ")"
							+ " PARAMETER STYLE JAVA"
							+ " LANGUAGE JAVA"
							+ " DETERMINISTIC"
							+ " NO SQL"
							+ " EXTERNAL NAME"
							+"        'Methods.walletExpired'";

			stmt.executeUpdate(createFunction_walletExpired);
			System.out.println("Created stored function to keep check on expired wallets");


			//TRIGGERS

			// If the attraction goes down for the day, all UPasses for the attraction are cancelled.
			String createTrigger_AttractionDown=
					"create trigger AttractionDown"
							+ " AFTER UPDATE OF running"
							+ " ON Attraction"
							+ " referencing old as AttractionRunning"
							+ " FOR EACH ROW MODE DB2SQL"
							+ "   DELETE FROM Upass WHERE attraction_id = AttractionRunning.attraction_id";

			stmt.executeUpdate(createTrigger_AttractionDown);
			System.out.println("Created trigger for Attraction being out of order to update Upass");

			// if the attraction goes down for the day, quantity of tickets available in Attraction table goes to 0
			String createTrigger_AttractionDown2=
					"create trigger AttractionDown2"
							+ " AFTER UPDATE OF running"
							+ " ON Attraction"
							+ " referencing new as AttractionDown"
							+ " FOR EACH ROW MODE DB2SQL"
							+ " WHEN (AttractionDown.running = FALSE)"
							+ "    UPDATE Attraction SET passes_available = 0 WHERE attraction_id = AttractionDown.attraction_id";

			stmt.executeUpdate(createTrigger_AttractionDown2);
			System.out.println("Created trigger for Attraction being out of order to update Attraction");

			// CURRENTLY NOT IN USE- WILL USE WHILE BUILDING APP
			String createTrigger_AttractionDownMessage=
					"create trigger AttractionDownMessage"
							+ " AFTER UPDATE OF running"
							+ " ON Attraction"
							+ " referencing old as AttractionDown"
							+ " FOR EACH ROW MODE DB2SQL"
							+ " WHEN (AttractionDown.running = FALSE)"
							+ "   CALL sendAttractionDownMessage(AttractionDown.attraction_name)";

			//stmt.executeUpdate(createTrigger_AttractionDownMessage);
			System.out.println("Created trigger for Attraction being out of order to update Attraction");

			// if an attraction is up running again after maintainance, all the passes_available are restored to default number of seats
			String createTrigger_AttractionUp=
					"create trigger AttractionUp"
							+ " AFTER UPDATE OF running"
							+ " ON Attraction"
							+ " referencing new as AttractionUp"
							+ " FOR EACH ROW MODE DB2SQL"
							+ " WHEN (AttractionUp.running = TRUE)"
							+ "    UPDATE Attraction SET passes_available = AttractionUp.passes_stored WHERE attraction_id = AttractionUp.attraction_id";

			stmt.executeUpdate(createTrigger_AttractionUp);
			System.out.println("Created trigger for Attraction being back up to update Attraction");

			// CURRENTLY NOT IN USE- WILL USE WHILE BUILDING APP
			String createTrigger_AttractionUpMessage=
					"create trigger AttractionUpMessage"
							+ " AFTER UPDATE OF running"
							+ " ON Attraction"
							+ " referencing new as AttractionUp"
							+ " FOR EACH ROW MODE DB2SQL"
							+ " WHEN (AttractionUp.running = TRUE)"
							+ "   CALL sendAttractionUpMessage(AttractionUp.attraction_name)";

			//stmt.executeUpdate(createTrigger_AttractionUpMessage);
			System.out.println("Created trigger for Attraction being back up to send notification to customers");

			//if current date is 24 hours after the visit date, delete the wallet
			String createTrigger_ExpiredWallet =
					"create trigger ExpiredWallet"
							+ " AFTER INSERT ON Wallet"
							+ " referencing new as newWallet"
							+ " FOR EACH ROW MODE DB2SQL"
							+ " WHEN (current_date > newWallet.visit_date)"
							+ "  CALL WalletExp()";
			stmt.executeUpdate(createTrigger_ExpiredWallet);
			System.out.println("Created trigger on Wallet for wallet expiration");


			//A new UPass cannot be issued if it would exceed the maximum number of passes for the wallet
			String createTrigger_ExceedsTotal=
					"create trigger ExceedsTotal"
							+ " AFTER insert ON Upass"
							+ " referencing new as oldUpass"
							+ " FOR EACH ROW MODE DB2SQL"
							+ " WHEN ((select count (*) as qwert from UPASS where (wallet_id = oldUpass.wallet_id)) > 4 )"
							+ " CALL WalletCap()";

			stmt.executeUpdate(createTrigger_ExceedsTotal);
			System.out.println("Created trigger for exceeding the maximum number of passes for the wallet");


		//	A new UPass cannot be issued if it would exceed the maximum number of passes per hour
			String createTrigger_ExceedsMaxPerHour=
					"create trigger ExceedsMaxPerHour"
							+ " AFTER INSERT ON Upass"
							+ " referencing new as oldUpass"
							+ " FOR EACH ROW MODE DB2SQL"
							+ "  when ((select count (*) from UPASS where ((wallet_id = oldUpass.wallet_id) AND ( HOUR(entry_time ) between HOUR(entry_time ) and HOUR (entry_time)+1))) > (select total_num_up from Wallet where wallet_id = oldUpass.wallet_id))"
							+ " CALL walletPerHour(oldUpass.wallet_id,oldUpass.entry_time)";

			stmt.executeUpdate(createTrigger_ExceedsMaxPerHour);
			System.out.println("Created trigger for exceeding the maximum number of passes per hour");

			//A new UPass results in decrease of passes available, and cannot be issued if exceeds
			String createTrigger_attrPassAvail=
					"create trigger attrPassAvail"
							+ " AFTER INSERT ON Upass"
							+ " referencing new as newUpass"
							+ " FOR EACH ROW MODE DB2SQL"
							+ " UPDATE Attraction SET passes_available = passes_available-1 WHERE attraction_id = newUpass.attraction_id";

			stmt.executeUpdate(createTrigger_attrPassAvail);
			System.out.println("Created trigger for decreasing the amount of passes available");

			//A deleted UPass results in the increase of passes available
			String createTrigger_attrPassAvail2=
					"create trigger attrPassAvail2"
							+ " AFTER DELETE ON Upass"
							+ " referencing OLD as oldUpass"
							+ " FOR EACH ROW MODE DB2SQL"
							+ " UPDATE Attraction SET passes_available = passes_available+1 WHERE attraction_id = oldUpass.attraction_id";

			stmt.executeUpdate(createTrigger_attrPassAvail2);
			System.out.println("Created trigger for increasing the amount of passes available");

			// Trigger to prevent customers to buy tickets after closing hours
			String createTrigger_BusinessHours=
					"create trigger BusinessHours"
							+ " AFTER INSERT ON Upass"
							+ " referencing new as newUpass"
							+ " FOR EACH ROW MODE DB2SQL"
							+ " WHEN ( (hour (newUpass.entry_time)) > 22)"
							+ "   CALL BusinessHours()";

			stmt.executeUpdate(createTrigger_BusinessHours);
			System.out.println("Created trigger for hours of operation");

			// Trigger to prevent customers to buy tickets before opening hours
			String createTrigger_BusinessHours2=
					"create trigger BusinessHours2"
							+ " AFTER INSERT ON Upass"
							+ " referencing new as newUpass"
							+ " FOR EACH ROW MODE DB2SQL"
							+ " WHEN ( (hour (newUpass.entry_time)) < 8)"
							+ "   CALL BusinessHours()";

			stmt.executeUpdate(createTrigger_BusinessHours2);
			System.out.println("Created trigger for hours of operation");


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}