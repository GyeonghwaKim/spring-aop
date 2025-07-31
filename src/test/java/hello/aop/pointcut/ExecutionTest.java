package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

@Slf4j
public class ExecutionTest {

    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello",String.class);
    }
    @Test
    void printMethod(){
        // public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        log.info("helloMethod = {}",helloMethod);
    }

    @Test
    void exactMatch(){
        pointcut.setExpression("execution(public java.lang.String hello.aop.member.MemberServiceImpl.hello(String))");
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    void allMatch(){
        pointcut.setExpression("execution(* *(..))");
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatch(){
        pointcut.setExpression("execution(* hello(..))");
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStar1(){
        pointcut.setExpression("execution(* hel*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStar2(){
        pointcut.setExpression("execution(* *el*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchFalse(){
        pointcut.setExpression("execution(* noono(..))");
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isFalse();
    }

    @Test
    void packageExactMatch1(){
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageExactMatch2(){
        pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }
    @Test
    void packageExactFalse(){
        pointcut.setExpression("execution(* hello.aop.*.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isFalse();
    }


    @Test
    void packageMatchSubPackage1(){
        pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageMatchSubPackage2(){
        pointcut.setExpression("execution(* hello.aop..*.*.*(..))");
        //pointcut.setExpression("execution(* hello.aop..*..*.*(..))");  이것도 가능
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    void typeMatchSuperType1(){
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    void typeMatchSuperType2(){
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isTrue();
    }

    @Test
    @DisplayName("자식 타입에만 있는 메서드")
    void typeMatchInternal1() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Method internal = MemberServiceImpl.class.getMethod("internal", String.class);
        Assertions.assertThat(pointcut.matches(internal,MemberServiceImpl.class)).isFalse();
    }

    @Test
    @DisplayName("자식 타입에만 있는 메서드")
    void typeMatchInternal2() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        Method internal = MemberServiceImpl.class.getMethod("internal", String.class);
        Assertions.assertThat(pointcut.matches(internal,MemberServiceImpl.class)).isTrue();
    }

    @Test
    void argsMatch(){
        pointcut.setExpression("execution(* *(String))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void argsMatchNoArgs(){
        pointcut.setExpression("execution(* *())");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    // 정확하게 하나의 파라미터만 허용 
    @Test
    void argsMatchStar(){
        pointcut.setExpression("execution(* *(*))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }


    //갯수 상관없이, 모든 타입 허용 , 파라미터 없어도 됨
    @Test
    void argsMatchAll(){
        pointcut.setExpression("execution(* *(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    //첫시작은 String 그 이후는 ...
    @Test
    void argsMatchComplex(){
        pointcut.setExpression("execution(* *(String, ..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

}
