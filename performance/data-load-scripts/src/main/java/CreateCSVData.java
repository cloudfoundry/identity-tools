import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.sql.Timestamp;
import java.util.UUID;

public class CreateCSVData{
    public static void main(String[] args) {
        System.out.println("Generating CSV files");
        int zones = 10;
        int usersPerZone = 100000;
        int clientsPerZone = 10000;
        printZones(zones);
        printUsers(zones, usersPerZone);
        printClients(zones, clientsPerZone);
        System.out.println("Files created!!");
    }

    public static void printZones(int numberOfZones) {
        StringBuffer csvData = new StringBuffer();
        csvData.append("id,created,lastmodified,version,subdomain,name,description,config\n");
        String config = "\"{\\\"clientLockoutPolicy\\\":{\\\"lockoutPeriodSeconds\\\":-1,\\\"lockoutAfterFailures\\\":-1,\\\"countFailuresWithin\\\":-1},\\\"tokenPolicy\\\":{\\\"accessTokenValidity\\\":-1,\\\"refreshTokenValidity\\\":-1,\\\"jwtRevocable\\\":false,\\\"refreshTokenUnique\\\":false,\\\"refreshTokenFormat\\\":\\\"jwt\\\",\\\"activeKeyId\\\":null},\\\"samlConfig\\\":{\\\"assertionSigned\\\":true,\\\"requestSigned\\\":true,\\\"wantAssertionSigned\\\":true,\\\"wantAuthnRequestSigned\\\":false,\\\"assertionTimeToLiveSeconds\\\":600},\\\"corsPolicy\\\":{\\\"xhrConfiguration\\\":{\\\"allowedOrigins\\\":[\\\".*\\\"],\\\"allowedOriginPatterns\\\":[],\\\"allowedUris\\\":[\\\".*\\\"],\\\"allowedUriPatterns\\\":[],\\\"allowedHeaders\\\":[\\\"Accept\\\",\\\"Authorization\\\",\\\"Content-Type\\\"],\\\"allowedMethods\\\":[\\\"GET\\\"],\\\"allowedCredentials\\\":false,\\\"maxAge\\\":1728000},\\\"defaultConfiguration\\\":{\\\"allowedOrigins\\\":[\\\".*\\\"],\\\"allowedOriginPatterns\\\":[],\\\"allowedUris\\\":[\\\".*\\\"],\\\"allowedUriPatterns\\\":[],\\\"allowedHeaders\\\":[\\\"Accept\\\",\\\"Authorization\\\",\\\"Content-Type\\\"],\\\"allowedMethods\\\":[\\\"GET\\\"],\\\"allowedCredentials\\\":false,\\\"maxAge\\\":1728000}},\\\"links\\\":{\\\"logout\\\":{\\\"redirectUrl\\\":\\\"/login\\\",\\\"redirectParameterName\\\":\\\"redirect\\\",\\\"disableRedirectParameter\\\":false,\\\"whitelist\\\":null},\\\"selfService\\\":{\\\"selfServiceLinksEnabled\\\":true,\\\"signup\\\":\\\"/create_account\\\",\\\"passwd\\\":\\\"/forgot_password\\\"}},\\\"prompts\\\":[{\\\"name\\\":\\\"username\\\",\\\"type\\\":\\\"text\\\",\\\"text\\\":\\\"Email\\\"},{\\\"name\\\":\\\"password\\\",\\\"type\\\":\\\"password\\\",\\\"text\\\":\\\"Password\\\"},{\\\"name\\\":\\\"passcode\\\",\\\"type\\\":\\\"password\\\",\\\"text\\\":\\\"One Time Code (Get on at /passcode)\\\"}],\\\"idpDiscoveryEnabled\\\":false,\\\"accountChooserEnabled\\\":false}\"";
        int i=0;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        System.out.println(ts.toString());
        while(i++ < numberOfZones) {
            csvData.append("perfzone" + i + "," + ts.toString() + "," + ts.toString() + ",0 ," +("perfzone" + i)+ "," +("perfzone" + i)+ ",Performance test zone," +config+"\n");
        }
        Path file = Paths.get("identity_zones.csv");
        try {
            Files.write(file, Arrays.asList(csvData.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printUsers(int numberOfZones, int numberOfUsers) {
        StringBuffer csvData = new StringBuffer();
        csvData.append("\"id\",\"created\",\"lastmodified\",\"version\",\"username\",\"password\",\"email\",\"authorities\",\"givenname\",\"familyname\",\"active\",\"phonenumber\",\"verified\",\"origin\",\"external_id\",\"identity_zone_id\",\"salt\",\"passwd_lastmodified\",\"legacy_verification_behavior\",\"passwd_change_required\",\"last_logon_success_time\",\"previous_logon_success_time\"\n");
        int i=0,j=0;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String date = ts.toString();
        String guid = "";
        while(i++ < numberOfZones){
            while(j++ < numberOfUsers) {
                guid = UUID.randomUUID().toString();
                csvData.append(guid+","+ date + "," +date+ ",0,user"+j+",$2a$10$v92nQ.g5dXQ1V1svF.KO4.I4YIWtzNlmnBGrJjB94wLheboASLaoG,user"+j+"@testcf.com,uaa.user,Perf"+j+"FN,Perf"+j+"LN,1,NULL,1,uaa,NULL,perfzone"+i+",NULL,"+date+",0,0,NULL,NULL\n");
            }
            j=0;
        }
        Path file = Paths.get("users.csv");
        try {
            Files.write(file, Arrays.asList(csvData.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printClients(int numberOfZones, int numberOfClients) {
        StringBuffer csvData = new StringBuffer();
        csvData.append("\"client_id\",\"resource_ids\",\"client_secret\",\"scope\",\"authorized_grant_types\",\"web_server_redirect_uri\",\"authorities\",\"access_token_validity\",\"refresh_token_validity\",\"additional_information\",\"autoapprove\",\"identity_zone_id\",\"lastmodified\",\"show_on_home_page\",\"app_launch_url\",\"app_icon\",\"created_by\",\"required_user_groups\"\n");
        int i=0,j=0;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String date = ts.toString();

        while(i++ < numberOfZones){
            while(j++ < numberOfClients) {
                csvData.append("\"client"+j+"\",\"none\",\"$2a$10$YhCmy5KLFs60yUn4.NgnFO4FsxxclNtwK8cEg8dBFUTvZgG20m4gG\",\"uaa.none\",\"client_credentials\",NULL,\"clients.read,clients.secret,idps.write,uaa.resource,zones.perfzone1.admin,clients.write,clients.admin,scim.write,idps.read,scim.read\",NULL,NULL,\"{\\\"allowedproviders\\\":[\\\"uaa\\\"],\\\"scopes\\\":[\\\"uaa.resource\\\",\\\"scim.read\\\"]}\",\"\",\"perfzone"+i+"\",\""+date+"\",1,NULL,NULL,NULL,NULL\n");
            }
            j=0;
        }
        Path file = Paths.get("clients.csv");
        try {
            Files.write(file, Arrays.asList(csvData.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}