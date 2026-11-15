package DAO;



import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import login.TaiKhoanDAOLoi;

public class TaiKhoanDAOLoiAllBugsTest {

    @BeforeAll
    static void chuanBi() throws Exception {
        TaiKhoanDAOLoiTest.chuanBiDuLieu();
    }

    @AfterAll
    static void donDep() throws Exception {
        TaiKhoanDAOLoiTest.donDepDuLieu();
        TaiKhoanDAOLoi.BUG = 0;
    }

    @TestFactory
    public List<DynamicContainer> testTatCaBug() {

        List<DynamicContainer> danhSach = new ArrayList<>();

        // Chạy lần lượt BUG từ 1 đến 11
        for (int bug = 1; bug <= 11; bug++) {

            final int bugHienTai = bug;

            List<DynamicTest> cacTest = new ArrayList<>();

            for (Method method : TaiKhoanDAOLoiTest.class.getDeclaredMethods()) {

                if (method.isAnnotationPresent(Test.class)) {

                    DynamicTest test = dynamicTest(
                        method.getName(),
                        () -> {

                            // Chọn lỗi cần kiểm tra
                            TaiKhoanDAOLoi.BUG = bugHienTai;

                            // Tạo đối tượng kiểm thử mới
                            TaiKhoanDAOLoiTest testClass =
                                new TaiKhoanDAOLoiTest();

                            try {
                                method.invoke(testClass);

                            } catch (InvocationTargetException e) {
                                throw e.getCause();
                            }
                        }
                    );

                    cacTest.add(test);
                }
            }

            danhSach.add(
                dynamicContainer("BUG " + bugHienTai, cacTest)
            );
        }

        return danhSach;
    }
}
