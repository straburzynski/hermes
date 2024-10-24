package pl.allegro.tech.hermes.management.domain.subscription.health.problem;

import static pl.allegro.tech.hermes.api.SubscriptionHealthProblem.unreachable;

import java.util.Optional;
import pl.allegro.tech.hermes.api.SubscriptionHealthProblem;
import pl.allegro.tech.hermes.management.domain.subscription.health.SubscriptionHealthContext;
import pl.allegro.tech.hermes.management.domain.subscription.health.SubscriptionHealthProblemIndicator;

public class UnreachableIndicator implements SubscriptionHealthProblemIndicator {
  private final double maxOtherErrorsRatio;
  private final double minSubscriptionRateForReliableMetrics;

  public UnreachableIndicator(
      double maxOtherErrorsRatio, double minSubscriptionRateForReliableMetrics) {
    this.maxOtherErrorsRatio = maxOtherErrorsRatio;
    this.minSubscriptionRateForReliableMetrics = minSubscriptionRateForReliableMetrics;
  }

  @Override
  public Optional<SubscriptionHealthProblem> getProblem(SubscriptionHealthContext context) {
    if (areSubscriptionMetricsReliable(context) && isOtherErrorsRateHigh(context)) {
      return Optional.of(
          unreachable(
              context.getOtherErrorsRate(),
              context.getSubscription().getQualifiedName().toString()));
    }
    return Optional.empty();
  }

  private boolean areSubscriptionMetricsReliable(SubscriptionHealthContext context) {
    return context.getSubscriptionRateRespectingDeliveryType()
        > minSubscriptionRateForReliableMetrics;
  }

  private boolean isOtherErrorsRateHigh(SubscriptionHealthContext context) {
    double otherErrorsRate = context.getOtherErrorsRate();
    return otherErrorsRate
        > maxOtherErrorsRatio * context.getSubscriptionRateRespectingDeliveryType();
  }
}
