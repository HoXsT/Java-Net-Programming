package client;

import interfaces.Result;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (Socket client = new Socket(host, port)) {
            System.out.println("Підключено до сервера.");

            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

            String classFilePath = "client/JobOne.class";
            out.writeObject(classFilePath);

            File file = new File("out/production/lr_5/" + classFilePath);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                byte[] b = new byte[fis.available()];
                fis.read(b);
                out.writeObject(b);
                fis.close();
            } else {
                System.out.println("Файл " + classFilePath + " не знайдено. Переконайтеся, що програма скомпільована.");
                return;
            }

            Scanner scanner = new Scanner(System.in);
            System.out.print("Введіть число N для обчислення факторіала: ");
            int num = scanner.nextInt();
            JobOne aJob = new JobOne(num);

            out.writeObject(aJob);
            System.out.println("Завдання відправлено на виконання.");

            // Створюємо об'єктний потік вводу для отримання результату
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());

            // Отримуємо ім'я class файлу результату та сам class файл
            String resultClassFile = (String) in.readObject();
            byte[] classBytes = (byte[]) in.readObject();

            // Зберігаємо class файл результату
            File resFile = new File("out/production/lr_5/" + resultClassFile);
            resFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(resFile);
            fos.write(classBytes);
            fos.close();

            Result r = (Result) in.readObject();

            System.out.println("Результат = " + r.output() + ", витрачений час = " + r.scoreTime() + " нс");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}