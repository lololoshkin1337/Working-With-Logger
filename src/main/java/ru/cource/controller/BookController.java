package ru.cource.controller;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.cource.helper.AuthorRepresentation;
import ru.cource.helper.GenreRepository;
import ru.cource.model.domain.Author;
import ru.cource.model.domain.Book;
import ru.cource.model.domain.enumOfGenres;
import ru.cource.model.service.BookShopService;



/**
 * Created by user on 12.11.2019.
 */
@Controller
@RequestMapping("/")
public class BookController {
    private static Logger logger= LoggerFactory.getLogger(BookController.class);

    @Autowired
    BookShopService bookShopService;

    @Autowired
    GenreRepository allStringgenres;

    @Autowired
    AuthorRepresentation authorRepresentation;

    @GetMapping("/")
    public String homeRedirect(){
    	System.out.println("redirect to Home");
        return "redirect:/Home";
    }
    
    @GetMapping("/Home")
    public String home(){
    	System.out.println("inside Controller");
        return "HomePage";
    }

    @GetMapping("/getAll")
    public String getAllUsers(Model model){
        model.addAttribute("books",bookShopService.GetAllBook());
        return "AllBooks";
    }


    @GetMapping("/CreateBook")
    //page in which we are creating new book
    //should have all possible GENRES
    public String addBook(Model model){
        model.addAttribute("AllGenres",allStringgenres.getAllGenres());
        return "CreateBookPage";
    }

    @PostMapping("/BookIsCreated")
    //page after which we have book
    //should create complete book and send it to service
    public String createBookPage(@RequestParam(value = "EnumOfGenre", required = false) Set<String> checkboxValues,
                             @RequestParam(value = "Authors", required = false) String Authors,
                             @ModelAttribute("book")Book book) {
        Set<Author> authors=authorRepresentation.getAuthorsFromString(Authors);
        Set<enumOfGenres> genres=new HashSet<enumOfGenres>();
        //Set<String> checkboxValues  не явно создает коллекцию
        //если нету значений то null а не пустая коллекция!!!
        if(checkboxValues!=null) {
            for (String s : checkboxValues) {
                genres.add(enumOfGenres.valueOf(s));
            }
        }
        //creating all authors and hand complete book to SERVICE
        book.setAuthors(authors);
        book.setGenre(genres);
        bookShopService.createBook(book);
        return "BookIsCreated";
    }

    @GetMapping("ChangeBook/{book_id}")
    public String changeBookPage(@PathVariable(value = "book_id") int Book_id, Model model){
        Book book=bookShopService.getById(Book_id);
        model.addAttribute("AllGenres",allStringgenres.getAllGenres());
        model.addAttribute("CheckedGenres",allStringgenres.toSetOfString(book.getGenre()));
        model.addAttribute("Book_name",book.getName());
        model.addAttribute("Book_id",book.getId());
        model.addAttribute("Authors",authorRepresentation.getString(book.getAuthors()));
        //send all data to user and wait new value to change the book
        //changing the book with new values
        return "ChangeBookPage";
    }
    @PostMapping("BookIsChanged/{book_id}")
    public String changingBook(@RequestParam(value = "EnumOfGenre", required = false) Set<String> checkboxValues,
                             @RequestParam(value = "Authors", required = false) String Authors,
                             @RequestParam(value = "NewName", required = false) String NewName,
                             @PathVariable(value = "book_id") int Book_id, Model model){
        Book book=bookShopService.getById(Book_id);
        Set<enumOfGenres> genres=new HashSet<enumOfGenres>();
        if(checkboxValues!=null) {
            for (String s : checkboxValues) {
                genres.add(enumOfGenres.valueOf(s));
            }
        }
        book.setGenre(genres);
        book.setName(NewName);
        book.setAuthors(authorRepresentation.getAuthorsFromString(Authors));
        model.addAttribute("book_id",book.getId());
        bookShopService.updateBook(book);
        return "BookIsChanged";
    }

    @GetMapping("/DeleteBook/{book_id}")
    public String deleteBookPage(@PathVariable(value = "book_id") int Book_id,Model model) {
        //should be here only to redirect POST request with id
        model.addAttribute("Book_id",Book_id);
        return "DeleteBookPage";
    }

    @PostMapping("/DeletingBook/{book_id}")
    public String deletingBook(@RequestParam(value = "descition", required = false) String decision,
                              @PathVariable(value = "book_id") int Book_id) {
        //user have made decision delete or not book
        if(decision.equals("YES")){
            bookShopService.deleteById(Book_id);
            return "redirect:/bookIsDeleted";
        }
        return "redirect:/getAll";
    }
    @GetMapping("/bookIsDeleted")
    //needed only for redirecting
    public String deletingOfBookIsConfirmd() {
    	return "bookIsDeleted";
    }	
}

