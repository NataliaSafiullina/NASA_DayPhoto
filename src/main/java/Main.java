import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.IOException;
import java.util.Scanner;
import java.io.FileOutputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;

public class Main {
    public static void main(String[] args) throws IOException {
        // вводим дату с клавиатуры пока в виде текста
        Scanner scanner = new Scanner(System.in);
        // TODO: разобраться как конвертировать строку в дату
//        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-mm-dd");
//        Date date = new Date();

        System.out.println("Введите дату в ормате YYYY-MM-DD: ");
        String strDate = scanner.nextLine();
//        System.out.println(strDate);
//        date = formatDate.parse(strDate);
//        System.out.println(date);

        // формируем строку ссылки для запроса, с ключом и датой
        String urlNasa = "https://api.nasa.gov/planetary/apod?" +
                "api_key=hGaaGXOsRYUsP9hECA6qaKTpuO0IBaHUWfIO3U31&" +
                "date="+strDate;
        // объявлем сущность, которая будет переводить (маппить) полученный ответ JSON в экземпляр нашего класса NASA
        ObjectMapper mapper = new ObjectMapper();

        // создаем http клиент
        CloseableHttpClient client = HttpClients.createDefault();
        // создаем http get запрос
        HttpGet request = new HttpGet(urlNasa);
        // запускаем запрос на исполнение и получаем ответ, ответ записываем
        CloseableHttpResponse response = client.execute(request);

//        Scanner scanner = new Scanner(response.getEntity().getContent());
//        System.out.println(scanner.nextLine());

        // маппим ответ в объект нашего класса NASA, в итоге в url будет записан адрес фотографии дня
        NasaAnswer answer = mapper.readValue(response.getEntity().getContent(), NasaAnswer.class);

        // формируем и отправлем новый запрос, который получит фото
        CloseableHttpResponse image = client.execute(new HttpGet(answer.url));

        // разбираем адрес фотографии на части и записываем по частям в массив
        String[] answerSplitted = answer.url.split("/");
        // создаем дискриптор файла с именем, которое будет в последнем элементе массива
        FileOutputStream file = new FileOutputStream(answerSplitted[answerSplitted.length - 1]);
        // содержание ответа записываем в файл
        image.getEntity().writeTo(file);
    }
}
