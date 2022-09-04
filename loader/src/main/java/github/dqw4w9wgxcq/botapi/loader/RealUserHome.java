package github.dqw4w9wgxcq.botapi.loader;

public class RealUserHome {
    public static String getUserHome() {
        String realUserHome = System.getProperty("real.user.home");
        if (realUserHome != null) {
            return realUserHome;
        } else {
            return System.getProperty("user.home");
        }
    }
}
