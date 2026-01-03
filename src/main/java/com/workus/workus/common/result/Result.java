package com.workus.workus.common.result;

import lombok.ToString;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 성공값 또는 실패값(오류)을 나타내는 불변(immutable) 컨테이너.
 *
 * <p>이 클래스는 성공 시 값 {@code T} 를, 실패 시 오류 {@code E} 를 가집니다.
 * 성공과 실패는 상호 배타적이며, 각각에 대응하는 헬퍼 메소드를 제공합니다.</p>
 *
 * @param <T> 성공일 때 담기는 값의 타입
 * @param <E> 실패일 때 담기는 오류의 타입
 */
@ToString
public final class Result<T, E> {
    private final T value;
    private final E error;

    private Result(T value, E error) {
        this.value = value;
        this.error = error;
    }

    public static <T, E> Result<T, E> success(T value) {
        return new Result<>(value, null);
    }
    public static <T, E> Result<T, E> failure(E error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }
    public boolean isFailure() {
        return error != null;
    }

    public T getOrThrow() {
        if (isFailure()) {
            throw new IllegalStateException("Failure: " + error);
        }
        return value;
    }
    public E getErrorOrThrow() {
        if (isSuccess()) {
            throw new IllegalStateException("Success has no error");
        }
        return error;
    }

    /**
     * 성공값에 함수를 적용하여 새로운 성공 결과로 매핑한다.
     * 실패인 경우 기존 오류를 그대로 유지한 실패 결과를 반환한다.
     *
     * @param mapper 변환 함수
     * @param <U> 매핑 후 성공값 타입
     * @return 변환된 성공 또는 기존 오류를 가진 실패 {@code Result}
     */
    public <U> Result<U, E> map(Function<T, U> mapper) {
        return isSuccess()
                ? Result.success(mapper.apply(value))
                : Result.failure(error);
    }
    /**
     * 성공값에 함수를 적용하여 새로운 {@code Result} 를 반환한다(바인딩).
     * 실패인 경우 기존 오류를 그대로 유지한 실패 결과를 반환한다.
     *
     * @param binder 성공값을 받아 {@code Result} 를 반환하는 함수
     * @param <U> 바인딩 후 성공값 타입
     * @return binder 가 반환한 {@code Result} 또는 기존 오류를 가진 실패 {@code Result}
     */
    public <U> Result<U, E> flatMap(Function<T, Result<U, E>> binder) {
        return isSuccess()
                ? binder.apply(value)
                : Result.failure(error);
    }
    /**
     * 오류값에 함수를 적용하여 오류 타입을 변환한다.
     * 성공인 경우 기존 성공값을 그대로 가진 성공 결과를 반환한다.
     *
     * @param mapper 오류 변환 함수
     * @param <F> 매핑 후 오류 타입
     * @return 변환된 오류 또는 기존 성공을 가진 {@code Result}
     */
    public <F> Result<T, F> mapError(Function<E,F> mapper) {
        return isFailure()
                ? Result.failure(mapper.apply(error))
                : Result.success(value);
    }
    /**
     * 성공/실패 각각에 대해 다른 함수를 적용하여 하나의 결과값으로 축약한다.
     *
     * @param onSuccess 성공 시 적용할 함수
     * @param onFailure 실패 시 적용할 함수
     * @param <R> 반환 타입
     * @return onSuccess 또는 onFailure 의 반환값
     */
    public <R> R fold(Function<? super T, ? extends R> onSuccess,
                      Function<? super E, ? extends R> onFailure) {
        return isSuccess()
                ? onSuccess.apply(value)
                : onFailure.apply(error);
    }

    public Result<T, E> onSuccess(Consumer<? super T> action) {
        if (isSuccess()) action.accept(value);
        return this;
    }
    public Result<T, E> onFailure(Consumer<? super E> action) {
        if (isFailure()) action.accept(error);
        return this;
    }
    public Result<T, E> onBoth(Consumer<Result<T, E>> action) {
        action.accept(this);
        return this;
    }
}
