
package utils;

import java.util.Date;
import java.util.Calendar;

public class FakeClock {
    public static Date now() {
        // Cambia este valor para simular una fecha posterior
        Calendar fakeNow = Calendar.getInstance();
        fakeNow.set(2025, Calendar.MAY, 30);  // Simula que hoy es 5 de abril
        return fakeNow.getTime();
    }
}
