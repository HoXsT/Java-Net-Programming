package server;

import interfaces.Executable;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер очікує на підключення на порту " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Клієнт підключився!");

                // об'єктний потік вводу для прийому інформації від клієнта
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                String classFile = (String) in.readObject();

                classFile = classFile.replaceFirst("client", "server");
                byte[] b = (byte[]) in.readObject();

                File file = new File("out/production/lr_5/" + classFile);
                file.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(b);
                fos.close();

                // Отримуємо об'єкт завдання
                Executable ex = (Executable) in.readObject();

                System.out.println("Початок обчислень...");
                double startTime = System.nanoTime();
                Object output = ex.execute();
                double endTime = System.nanoTime();
                double completionTime = endTime - startTime;
                System.out.println("Обчислення завершено.");

                // Формування об'єкта результату
                ResultImpl r = new ResultImpl(output, completionTime);
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

                String resultClassFile = "server/ResultImpl.class";
                out.writeObject(resultClassFile);

                File resFile = new File("out/production/lr_5/" + resultClassFile);
                if (resFile.exists()) {
                    FileInputStream fis = new FileInputStream(resFile);
                    byte[] bo = new byte[fis.available()];
                    fis.read(bo);
                    out.writeObject(bo);
                    fis.close();
                } else {
                    System.out.println("Файл " + resultClassFile + " не знайдено на сервері.");
                }

                out.writeObject(r);
                System.out.println("Результат відправлено клієнту.\n");

                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}