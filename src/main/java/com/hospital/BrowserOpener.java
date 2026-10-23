package com.hospital;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

// opens the website in the browser as soon as the app has started
@Component
public class BrowserOpener {

    @Value("${hospital.open-browser}")
    private boolean openBrowser;

    @Value("${server.port}")
    private String port;

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        if (!openBrowser) {
            return;
        }

        String url = "http://localhost:" + port;
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", url).start();
            } else {
                new ProcessBuilder("xdg-open", url).start();
            }
        } catch (Exception e) {
            System.out.println("Could not open the browser, go to " + url);
        }
    }
}
