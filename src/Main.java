import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));

        // 문자열 입력받기
        System.out.print("문자열 입력 > ");
        String input = bufferedReader.readLine();
        // 2자 이하 문자열은 substring 'bbb'를 포함할 수 없음
        if (input.length() < 3) {
            bufferedWriter.write("문법에 맞지 않는 문자열입니다.");
            bufferedWriter.flush();
            bufferedWriter.close();
            return;
        }

        String[] inputChar = input.split("");

        // 문자열이 문법에 맞는지 검사
        int i = 0, j = 0;
        boolean result = false;
        while (i < inputChar.length | j < 2) {
            // q0
            j = 0;
            if (inputChar[i].equals("a")) {
                i++;
            }
            else if (inputChar[i].equals("b")) {
                // q1
                i++; j++;
                if (i >= inputChar.length) break;
                if (inputChar[i].equals("a")) {
                    i++;
                }
                else if (inputChar[i].equals("b")){
                    // q2
                    i++; j++;
                    if (i >= inputChar.length) break;
                    if (inputChar[i].equals("a")) {
                        i++;
                    }
                    else if (inputChar[i].equals("b")){
                        // q3
                        result = true;
                        break;
                    }
                }
            }
        }

        if (result) bufferedWriter.write("문법에 맞는 문자열입니다.");
        else bufferedWriter.write("문법에 맞지 않는 문자열입니다.");

        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
