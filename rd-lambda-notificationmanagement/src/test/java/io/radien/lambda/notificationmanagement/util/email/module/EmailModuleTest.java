package io.radien.lambda.notificationmanagement.util.email.module;

import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailModuleTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    class MockAbstractModule extends EmailModule {
        @Override
        public <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz) {
            return mock(AnnotatedBindingBuilder.class);
        }

    }

    @Test
    public void testConfigure() {
        AnnotatedBindingBuilder mockBindingBuilder = mock(AnnotatedBindingBuilder.class);
        when(mockBindingBuilder.to(any(Class.class))).thenReturn(mock(ScopedBindingBuilder.class));
        MockAbstractModule module = mock(MockAbstractModule.class);
        doCallRealMethod().when(module).configure();
        when(module.bind(any())).thenReturn(mockBindingBuilder);
        module.configure();
        verify(module, times(5)).bind(any());
    }

}