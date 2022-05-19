import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Lunch {
    public static void main(String[] args) throws FileNotFoundException {
      File file = new File("C:\\Users\\220700jf\\AppData\\Local\\Temp\\Temp1_senior_data.zip\\senior_data\\s3\\s3.2-09.in");
      Scanner scan = new Scanner(file);
      int n = scan.nextInt();
      int[] p = new int[n];
      int[] w = new int[n];
      int[] d = new int[n];
      for(int i = 0; i < n; i++){
        p[i] = scan.nextInt();
        w[i] = scan.nextInt();
        d[i] = scan.nextInt();
      }
      System.out.println(minTime(n, p, w, d));
    }
  
    static long minTime(int n, int[] p, int[] w, int[] d){
      Calculate math = new Calculate(){
        
        @Override 
        public long timeSum(int s){
          long timeSum = 0;
          for(int i = 0; i < n; i++){
            if(Math.abs(s - p[i]) > d[i]){
              timeSum += (Math.abs(s - p[i]) - d[i]) * w[i];
            }
          }
          return timeSum;
        }
  
        @Override
        public double derivative(double s){
          double der = 0;
          for(int i = 0; i < n; i++){
            long pi = p[i];
            long wi = w[i];
            if(Math.abs(s - p[i]) > d[i]){
              der += wi * Math.abs(s - pi) / (s - pi);
            }
          }
          return der;
        }
      };
  
      int start = 1;
      int end = n < 2001 ? 2000 : 1000000000;
      while(end != start){
        int pos = (end + start) / 2;
        double leftDeriv = math.derivative(pos - 0.1);
        double rightDeriv = math.derivative(pos + 0.1);
        if((leftDeriv < 0 && rightDeriv > 0) || leftDeriv == 0){
          return math.timeSum(pos);
        }
        if(leftDeriv > 0) end = pos - 1;
        if(rightDeriv < 0) start = pos + 1;
      }
      return math.timeSum(start);
    }
}

interface Calculate{
  public long timeSum(int s);
  public double derivative(double s);

}
