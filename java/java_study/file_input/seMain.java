import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class seMain {
    ArrayList<Book> booklist = new ArrayList<>();

    void readbook(String s1) {
        Scanner filein = null;
        try {
            filein = new Scanner(new File (s1));
        } catch (FileNotFoundException e) {
            return ;
        }
        while (filein.hasNext()) {
            Book book = new Book();
            book.read(filein);
            booklist.add(book);
            book.print();
            
        }
    }

    void run (){
        this.readbook("books.txt");
        System.out.println("\n총 책 수: " + booklist.size() + "개");
    }


    public static void main (String[] args) {
        seMain m = new seMain();
        m.run();
    }
}