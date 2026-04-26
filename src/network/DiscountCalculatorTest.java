package ZeroCarbonFootprintTracker.src.network;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DiscountCalculatorTest {
    @Test 
    //setup
    public void testThirtyCalculateDiscount() {
        double total = 100.0;
        int discount= 30;
        double expected = 70.0; 
        //invoke 
        double price = DiscountCalculator.applyDiscount(total,discount);
        //analyse
        assertEquals(expected, price, 0.001);}
    
    @Test
    public void testCalculateDiscount() {
        double total = 100.0;
        int discount= 0;
        double expected = 100.0; 
        //invoke 
        double price = DiscountCalculator.applyDiscount(total,discount);
        //analyse
        assertEquals(expected, price, 0.001);}
    
    @Test
    public void test100CalculateDiscount() {
        double total = 100.0;
        int discount= 100;
        double expected = 70.0; 
        //invoke 
        double price = DiscountCalculator.applyDiscount(total,discount);
        //analyse
        assertEquals(expected, price, 0.001);}
    
    @Test
    public void test1CalculateDiscount() {
        double total = 100.0;
        int discount= 1;
        double expected = 99.0; 
        //invoke 
        double price = DiscountCalculator.applyDiscount(total,discount);
        //analyse
        assertEquals(expected, price, 0.001);}
    
    @Test
    public void testNegativeCalculateDiscount(){
        double total = 100.0;
        int discount= -1;
        double expected = 100.0; 
        //invoke 
        double price = DiscountCalculator.applyDiscount(total,discount);
        //analyse
        assertEquals(expected, price, 0.001);}
    }
