import java.io.*;
import java.util.*;

public class MiniCalc {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        StringTokenizer stringTokenizer;

        // 소스 파일 불러오기
        System.out.print("소스 코드 파일 경로 입력 > ");
        String filePath = bufferedReader.readLine();    // 소스 코드 파일 경로 입력 받기
        FileReader fileReader = new FileReader(filePath);
        bufferedReader = new BufferedReader(fileReader);

        int count = 0;

        String line;
        line = bufferedReader.readLine();
        stringTokenizer = new StringTokenizer(line);
        // 코드의 시작은 program이어야 한다.
        if (!stringTokenizer.nextToken().equals("program")) {
            System.out.println("error : 코드가 program으로 시작하지 않습니다.");
            return;
        }

        // var 키워드가 나온 이후에는 begin 키워드를 만날 때까지 변수 선언 파트
        // id table
        Map<String, Integer> idTable = new HashMap<>();

        line = bufferedReader.readLine();
        if (!line.equals("var")) {
            System.out.println("error : 변수 선언을 위해서 var 키워드가 나와야 합니다.");
            return;
        }
        while ((line = bufferedReader.readLine()) != null) {
            stringTokenizer = new StringTokenizer(line);
            String token = stringTokenizer.nextToken();
            // begin 키워드가 나오면 변수 선언 파트 종료
            if (line.equals("begin")) {
                break;
            }
            // int 키워드면 정수
            if (token.equals("int")) {
                // ; 가 나오면 한 줄 끝
                while (!(token = stringTokenizer.nextToken()).equals(";")) {
                    // 콤마는 변수이름을 나누는 용도
                    if (!token.equals(",")) {
                        // 첫 문자가 소문자인 경우 변수이름 조건 1차 통과
                        if (token.charAt(0) >= 97 && token.charAt(0) <= 122) {
                            // 토큰이 소문자로 시작하면, 사용자 정의어의 형식에 맞는지 확인
                            String id = String.valueOf(token.charAt(0));
                            int i = 1;
                            while (i < token.length()) {
                                // id 토큰에 문자가 등장할 경우, substring으로 쪼개기
                                if (isEndOfId(token.charAt(i))) {
                                    idTable.put(id, 1);    // 심볼 테이블에 입력
                                    count++;    // 사용자 정의어 개수 count
                                    break;
                                }
                                id = id + token.charAt(i);
                                i++;
                            }
                            // 해당 토큰에 문자가 등장하지 않고 모두 사용자 정의어(id)인 경우
                            if (i == token.length()) {
                                idTable.put(id, 1);
                                count++;
                            }
                            else {
                                System.out.println("error : " + token + "은 사용자 정의어 형식에 맞지 않는 변수 이름입니다.");
                                return;
                            }
                        }
                        else {
                            System.out.println("error : 변수이름은 영소문자로 시작되어야 합니다.");
                            return;
                        }
                    }
                }
            }
            // float 키워드면 실수
            else if (token.equals("float")) {
                // ; 가 나오면 한 줄 끝
                while (!(token = stringTokenizer.nextToken()).equals(";")) {
                    // 콤마는 변수이름을 나누는 용도
                    if (!token.equals(",")) {
                        // 첫 문자가 소문자인 경우 변수이름 조건 1차 통과
                        if (token.charAt(0) >= 97 && token.charAt(0) <= 122) {
                            // 토큰이 소문자로 시작하면, 사용자 정의어의 형식에 맞는지 확인
                            String id = String.valueOf(token.charAt(0));
                            int i = 1;
                            while (i < token.length()) {
                                // id 토큰에 문자가 등장할 경우, substring으로 쪼개기
                                if (isEndOfId(token.charAt(i))) {
                                    idTable.put(id, 2);    // 심볼 테이블에 입력
                                    count++;    // 사용자 정의어 개수 count
                                    break;
                                }
                                id = id + token.charAt(i);
                                i++;
                            }
                            // 해당 토큰에 문자가 등장하지 않고 모두 사용자 정의어(id)인 경우
                            if (i == token.length()) {
                                idTable.put(id, 2);
                                count++;
                            }
                            else {
                                System.out.println("error : " + token + "은 사용자 정의어 형식에 맞지 않는 변수 이름입니다.");
                                return;
                            }
                        }
                        else {
                            System.out.println("error : 변수이름은 영소문자로 시작되어야 합니다.");
                            return;
                        }
                    }
                }
            }
            else {
                System.out.println("error : 변수 형식은 " + token + "이 아니라 int나 float만 가능합니다.");
                return;
            }
        }

        // begin을 만났을 때 계산 코드 파트 시작
        Stack<String> stk = new Stack<>();
        Map<String, String> resultTable = new HashMap<>();
        // end 키워드를 만날 때 까지 진행
        while (!(line = bufferedReader.readLine()).equals("end")) {
            stringTokenizer = new StringTokenizer(line);
            String token = stringTokenizer.nextToken();
            if (token.equals(";")) continue;
            else if (token.equals("print")) {
                System.out.println(idTable.toString());
                System.out.println(resultTable.toString());
                System.out.println("result : " + resultTable.get(stringTokenizer.nextToken()));
                return;
            }
            else {
                // 변수선언 파트에서 선언된 변수인 경우 pass, 그렇지 않으면 error
                if (idTable.containsKey(token)) {
                    if (stringTokenizer.nextToken().equals("=")) {
                        String val = stringTokenizer.nextToken();
                        // 숫자인 경우, 변수에 숫자 저장(resultTable)
                        if (val.charAt(0) >= 48 && val.charAt(0) <= 57) {
                            resultTable.put(token, val);
                        }
                        // 숫자가 아닌 경우, ";"를 만날 때까지 계산
                        else {
                            String postfix = ""; // 후위표기법으로 계산
                            do {
                                // 연산자일 경우
                                if (val.equals("+")|val.equals("-")) {
                                    if (stk.isEmpty()) stk.push(val);
                                    else {
                                        String temp = stk.pop();
                                        stk.push(val);
                                        postfix.concat(temp + " ");
                                    }
                                    stk.push(val);
                                } else if (val.equals("*")|val.equals("/")) {
                                    if (stk.isEmpty()) stk.push(val);
                                    else {
                                        if (stk.peek().equals("+")|stk.peek().equals("-")) stk.push(val);
                                        else {
                                            String temp = stk.pop();
                                            stk.push(val);
                                            postfix.concat(temp + " ");
                                        }
                                    }
                                }
                                // 변수는 그대로 표현
                                else if (idTable.containsKey(val)) {
                                    postfix.concat(val + " ");
                                } else {
                                    System.out.println("error : " + val + "은 변수가 아닙니다.");
                                    return;
                                }
                            } while(!(val = stringTokenizer.nextToken()).equals(";"));
                            // 후위표기법으로 모두 나타냈으면 연산한 값을 token에 저장
                            StringTokenizer st = new StringTokenizer(postfix);
                            String value;
                            Stack<String> stack = new Stack<>();
                            while (st.hasMoreTokens()) {
                                String element;
                                switch ((value = st.nextToken())) {
                                    case "+":
                                        String n1 = stack.pop();
                                        String n2 = stack.pop();
                                        int int_num; double float_num;
                                        if (idTable.get(n1) == 1) {
                                            int_num = Integer.parseInt(n1);
                                            if (idTable.get(n2) == 1) {
                                                int_num += Integer.parseInt(n2);
                                                element = Integer.toString(int_num);
                                            }
                                            else {
                                                float_num = (double)int_num + Double.parseDouble(n2);
                                                element = Double.toString(float_num);
                                            }
                                        }
                                        else {
                                            float_num = Double.parseDouble(n1);
                                            float_num += Double.parseDouble(n2);
                                            element = Double.toString(float_num);
                                        }
                                        stack.push(element);
                                        break;
                                    case "-":
                                        n1 = stack.pop();
                                        n2 = stack.pop();
                                        if (idTable.get(n2) == 1) {
                                            int_num = Integer.parseInt(n1);
                                            if (idTable.get(n1) == 1) {
                                                int_num -= Integer.parseInt(n1);
                                                element = Integer.toString(int_num);
                                            }
                                            else {
                                                float_num = (double)int_num - Double.parseDouble(n1);
                                                element = Double.toString(float_num);
                                            }
                                        }
                                        else {
                                            float_num = Double.parseDouble(n2);
                                            float_num -= Double.parseDouble(n1);
                                            element = Double.toString(float_num);
                                        }
                                        stack.push(element);
                                        break;
                                    case "*":
                                        n1 = stack.pop();
                                        n2 = stack.pop();
                                        if (idTable.get(n1) == 1) {
                                            int_num = Integer.parseInt(n1);
                                            if (idTable.get(n2) == 1) {
                                                int_num *= Integer.parseInt(n2);
                                                element = Integer.toString(int_num);
                                            }
                                            else {
                                                float_num = (double)int_num * Double.parseDouble(n2);
                                                element = Double.toString(float_num);
                                            }
                                        }
                                        else {
                                            float_num = Double.parseDouble(n1);
                                            float_num *= Double.parseDouble(n2);
                                            element = Double.toString(float_num);
                                        }
                                        stack.push(element);
                                        break;
                                    case "/":
                                        n1 = stack.pop();
                                        n2 = stack.pop();
                                        if (idTable.get(n2) == 1) {
                                            int_num = Integer.parseInt(n1);
                                            if (idTable.get(n1) == 1) {
                                                int_num /= Integer.parseInt(n1);
                                                element = Integer.toString(int_num);
                                            }
                                            else {
                                                float_num = (double)int_num / Double.parseDouble(n1);
                                                element = Double.toString(float_num);
                                            }
                                        }
                                        else {
                                            float_num = Double.parseDouble(n2);
                                            float_num /= Double.parseDouble(n1);
                                            element = Double.toString(float_num);
                                        }
                                        stack.push(element);
                                        break;
                                    default:
                                        stack.push(value);
                                }
                                value = stack.pop();
                                resultTable.put(token, value);
                            }
                        }
                    }
                    else {
                        System.out.println("error : 변수명 뒤에는 =이 나와야 합니다.");
                        return;
                    }
                }
                else {
                    System.out.println("error : " + token + "은 변수가 아닙니다.");
                    return;
                }
            }
        }
    }

    // hashmap에서 value에 해당하는 key가 존재하는지 확인, 존재한다면 key return
    public static <K, V> K getKey(Map<K, V> map, V value) {
        // 찾을 hashmap 과 주어진 단서 value
        for (K key : map.keySet()) {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }

    // a-z, _, 0-9, A-Z가 아님 확인
    public static boolean isEndOfId(char ch) {
        boolean result = false;
        if (ch >= 65 && ch <= 90);  // A-Z
        else if (ch >= 97 && ch <= 122);    // a-z
        else if (ch == '_');        // _
        else if (ch >= 48 && ch <= 57); // 0-9
        else result = true;
        return result;
    }

}
