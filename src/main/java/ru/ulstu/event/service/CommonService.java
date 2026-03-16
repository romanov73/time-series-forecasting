package ru.ulstu.event.service;


import org.springframework.stereotype.Service;
import ru.ulstu.event.model.Event;
import ru.ulstu.event.model.ForecastResult;
import ru.ulstu.event.model.History;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommonService {
    private final DataPreprocessor dataPreprocessor;
    private final MarkovChainPredictor markovChainPredictor;

    public CommonService(DataPreprocessor dataPreprocessor,
                         MarkovChainPredictor markovChainPredictor) {
        this.dataPreprocessor = dataPreprocessor;
        this.markovChainPredictor = markovChainPredictor;
    }

    private void trainModel(List<Event> events) {
        List<String> allEventTypes = getAllEventTypes(events);

        // Создаем словарь событий
        Map<String, Integer> vocabulary = dataPreprocessor.createEventVocabulary(allEventTypes);
        Map<Integer, String> reverseVocabulary = dataPreprocessor.createReverseVocabulary(vocabulary);

        // Конвертируем события в числовую последовательность
        List<Integer> eventSequence = dataPreprocessor.convertEventsToSequence(events, vocabulary);

        // Обучаем модель
        markovChainPredictor.train(eventSequence, vocabulary.size());
        markovChainPredictor.setReverseVocabulary(reverseVocabulary);

        System.out.println("Vocabulary size: " + vocabulary.size());
    }

    private ForecastResult predictNextEvent(List<Event> events, String lastEventType) {
        Map<String, Integer> vocabulary = dataPreprocessor.createEventVocabulary(
                getAllEventTypes(events)
        );

        Integer lastEventCode = vocabulary.get(lastEventType);
        if (lastEventCode == null) {
            System.err.println("Unknown event type: " + lastEventType);
            return null;
        }

        return markovChainPredictor.predictNextEvent(lastEventCode);
    }

    private List<String> getAllEventTypes(List<Event> events) {
        return events
                .stream()
                .map(Event::getEventType)
                .collect(Collectors.toSet())
                .stream()
                .toList();
    }

    public ForecastResult trainAndPredict(History history) {
        trainModel(history.getEvents());

        // Прогнозирование следующего события
        ForecastResult prediction = predictNextEvent(history.getEvents(),
                history.getEvents().getLast().getEventType());

        if (prediction != null) {
            System.out.println("Predicted next event: " + prediction.getForecastEvent());
            System.out.println("Confidence: " + prediction.getConfidence());
        } else {
            System.out.println("Could not make prediction");
        }
        return prediction;
    }
}
