package edu.school21.tanks.app;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import edu.school21.tanks.repositories.PlayersRepository;
import edu.school21.tanks.server.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class Main {
    public static PlayersRepository playersRepository;
    public static void main(String[] args) throws IOException, InterruptedException {
        Args arguments = new Args();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);
        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        Server server = new Server(arguments.port, context);
        playersRepository = context.getBean("playersRepository", PlayersRepository.class);
        playersRepository.delete(0L);
        playersRepository.delete(1L);
        server.start();
    }

    @Parameters(separators = "=")
    static class Args {
        @Parameter(
                names = "--port",
                required = true
        )
        private int port;
    }
}
