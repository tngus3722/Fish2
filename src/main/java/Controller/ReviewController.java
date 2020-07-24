package Controller;

import Domain.Review;
import Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ReviewController {
    @Autowired
    ReviewService reviewService;

    @ResponseBody
    @RequestMapping(value = "/board", method = RequestMethod.GET) // 해당 낚시터 리뷰 조회
    public ResponseEntity getBoard(@RequestParam("id") Integer id){
        return new ResponseEntity(reviewService.display(id), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value ="/board" , method= RequestMethod.POST) // 해당 낚시터 리뷰 발행
    public ResponseEntity postBoard(@RequestBody Review review, HttpServletRequest request){
        reviewService.insert(review, request);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ResponseBody
    @RequestMapping(value = "/board", method = RequestMethod.DELETE) // 해당 낚시터 리뷰 삭제
    public ResponseEntity deleteBoard(@RequestBody Review review,  HttpServletRequest request){
        if ( reviewService.delete(review, request) )
            return new ResponseEntity("delete success" , HttpStatus.OK);
        else
            return new ResponseEntity("delete fail",HttpStatus.FORBIDDEN);
    }

    @ResponseBody
    @RequestMapping(value = "/board", method = RequestMethod.PUT) // 해당 낚시터 리뷰 수정
    public ResponseEntity updateBoard(@RequestBody Review review,  HttpServletRequest request){
        if ( reviewService.update(review, request) )
            return new ResponseEntity("update success" , HttpStatus.OK);
        else
            return new ResponseEntity("update fail",HttpStatus.FORBIDDEN);
    }

}
