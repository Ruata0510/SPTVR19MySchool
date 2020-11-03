/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.manager;

import entity.Journal;
import entity.Person;
import entity.Subject;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import tools.intefaces.Saveable;
import tools.savers.SaveToFile;

/**
 *
 * @author user
 */
public class JournalManager implements Serializable{
    private Scanner scanner = new Scanner(System.in);
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY");
    public void setMarkToUser(List<Subject> listSubjects,List<Person> listPersons, List<Journal> listJournals){
        System.out.println("--- Выставить оценку ---");
        System.out.println("Список предметов: ");
        for (int i = 0; i < listSubjects.size(); i++) {
            Subject subject = listSubjects.get(i);
            System.out.printf("%d. %s. Часов: %d Преподаватель: %s %s%n"
                    ,i+1
                    ,subject.getName()
                    ,subject.getDuration()
                    ,subject.getTeacher().getFirstname()
                    ,subject.getTeacher().getLastname()
            );
        }
        System.out.print("Выбрать предмет: ");
        int[] range = new int[]{0,listSubjects.size()};
        int numSubject=getNumInRange("Выберите номер предмета: ", range);
        Subject subject = listSubjects.get(numSubject-1);
        System.out.println("Список учеников:");
        for (int i = 0; i < listPersons.size(); i++) {
            Person person = listPersons.get(i);
            int mark;
            int indexJournal = getOneJournal(person, subject, listJournals);
            String dateStr = "";
            if(indexJournal<0){
                mark = 0;
            }else{
                mark = listJournals.get(indexJournal).getMark();
                dateStr = sdf.format(listJournals.get(indexJournal).getDate());
            }
            if("STUDENT".equals(person.getRole())){
                System.out.printf("%d. %s %s, оценка по предмету: %s - %d, дата: %s%n"
                        ,i+1
                        ,person.getFirstname()
                        ,person.getLastname()
                        ,subject.getName()
                        ,mark
                        ,dateStr
                );
            }
        }
        System.out.print("Выбрать ученика: ");
        range = new int[]{0,listPersons.size()};
        int numStudent= getNumInRange("Выберите номер студента: ", range);
        Person student = listPersons.get(numStudent-1);
        range = new int[]{0,5};
        System.out.print("Оценка: ");
        int mark = getNumInRange("Выберите оценку от 1 до 5: ", range);
        Calendar c = new GregorianCalendar();
        int indexJournals = getOneJournal(student, subject, listJournals);
        if(indexJournals < 0){
            Journal newOrUpdateJournal = new Journal();
            newOrUpdateJournal.setSubject(subject);
            newOrUpdateJournal.setStudent(student);
            newOrUpdateJournal.setMark(mark);
            newOrUpdateJournal.setDate(c.getTime());
            listJournals.add(newOrUpdateJournal);
        }else{
            listJournals.get(indexJournals).setMark(mark);
            listJournals.get(indexJournals).setDate(c.getTime());
        }
        Saveable saveable = new SaveToFile();
        saveable.saveToFile(listJournals, "listJournals");
        
    }

    private int getOneJournal(Person person, Subject subject, List<Journal> listJournals) {
        for (int i = 0; i < listJournals.size(); i++) {
            Journal journal = listJournals.get(i);
            if("STUDENT".equals(person.getRole()) && person.equals(journal.getStudent())
                && subject.equals(journal.getSubject())){
                
                return i;
            }
        }
        return -1;
    }
    
    private int getNumInRange(String text, int[] range){
        int num=-1;
        do{
            String numStr = scanner.nextLine();
            try {
                num = Integer.parseInt(numStr);
                if(num > range[0] && num <= range[1]){
                    return num;
                }else{
                    throw new Exception();
                }
            } catch (Exception e) {
                System.out.println(text);
            }
        }while(true);
    }

    public void printMarksUser(List<Person> listPersons,List<Journal> listJournals) {
        System.out.println("--- Список учеников ---");
        for (int i = 0; i < listPersons.size(); i++) {
            Person person = listPersons.get(i);
            if("STUDENT".equals(person.getRole())){
                System.out.printf("%d. %s %s%n"
                        ,i+1
                        ,person.getFirstname()
                        ,person.getLastname()
                );
            }
        }
        System.out.println("Выберите номер ученика");
        int[] range = {0,listPersons.size()};
        int numStudent = this.getNumInRange("Выберите из списка учеников", range);
        Person student = listPersons.get(numStudent-1);
        for (int i = 0; i < listJournals.size(); i++) {
            Journal journal = listJournals.get(i);
            if(journal.getStudent().equals(student)){
                System.out.printf("%d. %s %s. Предмет \"%s\". Оценка: %d%n"
                        ,i+1
                        ,journal.getStudent().getFirstname()
                        ,journal.getStudent().getLastname()
                        ,journal.getSubject().getName()
                        ,journal.getMark()
                );
            }
        }
    }

    public void printMarksForSubject(List<Journal> listJournals, List<Subject>listSubjects) {
        System.out.println("Список предметов: ");
        for (int i = 0; i < listSubjects.size(); i++) {
            System.out.printf("%d. %s%n",i+1,listSubjects.get(i).getName());
        }
        System.out.println("Выберите номер предмета: ");
        int[] range ={0, listJournals.size()};
        int numSubject = getNumInRange("Выберите номер из списка", range);
        Subject subject = listSubjects.get(numSubject - 1);
        for (int i = 0; i < listJournals.size(); i++) {
            if(!subject.equals(listJournals.get(i).getSubject())) continue;
            System.out.printf("%d. %s %s. Предмет \"%s\". Оценка: %d%n"
                    ,i+1
                    ,listJournals.get(i).getStudent().getFirstname()
                    ,listJournals.get(i).getStudent().getLastname()
                    ,listJournals.get(i).getSubject().getName()
                    ,listJournals.get(i).getMark()
            );
            
        }
        
        
    }
}
