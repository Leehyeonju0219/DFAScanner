import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DFAMinimizing {
    public static void main(String[] args) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        StringTokenizer stringTokenizer;

        System.out.print("파일 경로 입력 > ");
        String path = bufferedReader.readLine();
        FileReader fileReader = new FileReader(path);

        // 파일을 한 줄씩 읽어와서 arrayList에 저장
        List<String> tempTable = new ArrayList<>();
        bufferedReader = new BufferedReader(fileReader);
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            tempTable.add(line);
        }

        // 맨 첫줄을 제외하고 공백을 기준으로 토큰을 쪼개서 character 배열에 넣기
        char[][] inputTable = new char[tempTable.size()][3];    // 2차원 char 배열
        for (int i = 1; i < tempTable.size(); i++) {            // 첫 줄 제외
            stringTokenizer = new StringTokenizer(tempTable.get(i));    // 공백을 기준으로 토큰 쪼개기
            for (int j = 0; j < 3; j++) {
                inputTable[i-1][j] = stringTokenizer.nextToken().charAt(0);
            }
        }

        // 동치류 테이블을 이용하여 DFA 최소화(2차원 배열 생성)
        int[][] equalTable = new int[tempTable.size()][4];  // 동치류, 동치류 이름(group number), a, b
        // 동치류 테이블 초기화
        for (int i = 0; i < tempTable.size(); i++) {
            equalTable[i][0] = i+1;                         // 동치류를 숫자로 표현(ex_A=1, B=2, C=3,...)
            for (int j = 1; j < 4; j++) {
                if (i == tempTable.size()-1 && j == 1) equalTable[i][j] = 2;
                else if (inputTable[i][j-1] == inputTable[tempTable.size()-1][0]) equalTable[i][j] = 2;
                else equalTable[i][j] = 1;
            }
        }

        // 동치류가 더 이상 구별되지 않을 때까지 분할 반복
        boolean finish = false;     // finish가 true가 되면(분할이 끝나면) 반복문 중단
        while (!finish) {
            int a = equalTable[0][2], b = equalTable[0][3];       // a를 만났을 때 동치류, b를 만났을 때 동치류
            for (int i = 0; i < tempTable.size(); i++) {
                if (a != equalTable[i][2]) {
                    int temp = equalTable[i][2];
                    int temp_idx = 0;
                    for (int j = i; j < tempTable.size(); j++) {
                        if (temp == equalTable[j][1]) {
                            temp_idx = j;
                            equalTable[j][1]++;
                        }
                        else if (temp < equalTable[j][1]) equalTable[j][1]++;
                    }
                    equalTable[i][2] = equalTable[temp_idx][1];
                }
            }
        }


    }
}
