package testmaven;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {

    App app = new App();

    // Test 1: Kiểm tra số 2 là số chẵn
    @Test
    public void testSoChan2() {
        assertTrue(app.isEventNumber(2));
    }

    // Test 2: Kiểm tra số 4 là số chẵn
    @Test
    public void testSoChan4() {
        assertTrue(app.isEventNumber(4));
    }
}
