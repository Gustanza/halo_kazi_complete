package tz.co.halotel.halokazi.session;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import tz.co.halotel.halokazi.common.error.ApiException;
import tz.co.halotel.halokazi.common.error.ErrorCode;

/** Supplies {@code @CurrentUser AuthenticatedUser} parameters from the interceptor's request attribute. */
class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && AuthenticatedUser.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Object user = webRequest.getAttribute(SessionAuthInterceptor.ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (user == null) {
            // A @CurrentUser parameter on a route excluded from authentication.
            throw new ApiException(ErrorCode.UNAUTHENTICATED);
        }
        return user;
    }
}
