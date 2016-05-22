/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sp1d.chym.loader;

import java.util.Set;
import java.util.TreeSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Sp1D
 */
public class Main {

    static ApplicationContext ctx;
    static Loader loader;

    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        
        System.setProperty("spring.profiles.active", "prod");

        Set<Command> commands = new TreeSet<>();
        try {
            for (String arg : args) {
                commands.add(Command.valueOf(arg.toUpperCase()));
            }
        } catch (IllegalArgumentException ex) {
            System.out.print("Error in command line. Acceptable commands: ");
            for (int i = 0; i < Command.values().length; i++) {
                System.out.print(Command.values()[i].toString().toLowerCase());
                if (i < Command.values().length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println(".");
            System.exit(0);
        }

        
        ctx = new AnnotationConfigApplicationContext("net.sp1d.chym.loader");
        loader = ctx.getBean(Loader.class);

        for (Command command : commands) {
            action(command);
        }

        exit();

    }

    private static void action(Command command) {
        switch (command) {
            case LOAD:
                loader.init();
                loader.load();
                break;
            case FETCH:
                loader.init();
                loader.fetch();
                break;
            case RESET:
                loader.init();
                loader.reset();
                break;
            case UPDATE:
                loader.init();
                loader.update();
                break;
            case UPDATERSS:
                loader.init();
                loader.rssUpdate();
                break;
        }
    }

    private static void exit() {
        loader.exit();
    }

    enum Command {
        RESET, LOAD, FETCH, UPDATE, UPDATERSS;
    }

}
