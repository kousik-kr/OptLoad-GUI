"""
Generate synthetic VRP-LU query sets with more natural scheduling patterns.

Compared to the baseline generator, this script stitches together pickup and
 drop-off windows along a plausible day-long timeline. It adds realistic
 turnaround buffers, clusters services around a handful of hubs, and aligns
 vehicle capacity with the expected shipment mix.
"""
from __future__ import annotations

import argparse
import random
from dataclasses import dataclass
from typing import List, Sequence, Tuple

NODE_ID_MIN = 0
NODE_ID_MAX = 285_049
WORK_START = 8 * 60  # 08:00
WORK_END = 19 * 60  # 19:00
DEFAULT_NUM_QUERIES = 20
DEFAULT_SERVICES_PER_QUERY = 20


@dataclass(frozen=True)
class ServiceProfile:
    """Defines a family of services with similar timing and amount traits."""

    name: str
    amount_range: Tuple[int, int]
    pickup_duration: Tuple[int, int]
    dropoff_duration: Tuple[int, int]
    travel_buffer: Tuple[int, int]
    turnaround_buffer: Tuple[int, int]


class NaturalQueryGenerator:
    def __init__(
        self,
        rng: random.Random,
        hubs: Sequence[int] | None = None,
    ) -> None:
        self.rng = rng
        self.hubs = list(hubs) if hubs else self._generate_hubs()

    def _generate_hubs(self) -> List[int]:
        """Create a few anchor nodes that services cluster around."""
        hub_count = self.rng.randint(3, 6)
        return [self.rng.randint(NODE_ID_MIN, NODE_ID_MAX) for _ in range(hub_count)]

    def _pick_near_hub(self) -> int:
        hub = self.rng.choice(self.hubs)
        offset = self.rng.randint(-500, 500)
        return max(NODE_ID_MIN, min(NODE_ID_MAX, hub + offset))

    def _build_service_windows(
        self, profile: ServiceProfile, earliest_start: int
    ) -> Tuple[Tuple[int, int], Tuple[int, int], int]:
        """Create pickup and drop-off windows that fit within the workday."""
        for _ in range(200):
            pickup_duration = self.rng.randint(*profile.pickup_duration)
            dropoff_duration = self.rng.randint(*profile.dropoff_duration)
            travel_buffer = self.rng.randint(*profile.travel_buffer)

            latest_pickup_start = WORK_END - (dropoff_duration + travel_buffer + pickup_duration + 15)
            if latest_pickup_start <= WORK_START:
                continue

            jitter = self.rng.randint(-15, 45)
            pickup_start = max(WORK_START, min(earliest_start + jitter, latest_pickup_start))
            pickup_end = pickup_start + pickup_duration

            dropoff_start_floor = pickup_end + travel_buffer
            dropoff_start_ceil = min(dropoff_start_floor + 60, WORK_END - dropoff_duration)
            if dropoff_start_floor >= dropoff_start_ceil:
                continue

            dropoff_start = self.rng.randint(dropoff_start_floor, dropoff_start_ceil)
            dropoff_end = dropoff_start + dropoff_duration
            if dropoff_end > WORK_END:
                continue

            amount = self.rng.randint(*profile.amount_range)
            return (pickup_start, pickup_end), (dropoff_start, dropoff_end), amount

        raise RuntimeError("Unable to build a feasible service window")

    def generate_query(self, services_per_query: int, profiles: Sequence[ServiceProfile]) -> str:
        depot = self._pick_near_hub()
        query_lines = [f"D {depot}"]

        earliest_start = WORK_START + self.rng.randint(0, 90)
        services: List[str] = []
        total_amount = 0

        for _ in range(services_per_query):
            profile = self.rng.choice(profiles)
            pickup_window, dropoff_window, amount = self._build_service_windows(
                profile, earliest_start
            )

            pickup_node = self._pick_near_hub()
            dropoff_node = self._pick_near_hub()

            services.append(
                f"S {pickup_node},{dropoff_node} "
                f"{pickup_window[0]},{pickup_window[1]} {dropoff_window[0]},{dropoff_window[1]} {amount}"
            )
            total_amount += amount
            earliest_start = dropoff_window[1] + self.rng.randint(*profile.turnaround_buffer)

        capacity_target = max(total_amount + self.rng.randint(2, 6), int(total_amount * 0.65))
        capacity = min(max(capacity_target, 8), 24)
        query_lines.insert(1, f"C {capacity}")
        query_lines.extend(services)
        return "\n".join(query_lines)

    def generate_queries(self, num_queries: int, services_per_query: int) -> List[str]:
        profiles = [
            ServiceProfile(
                name="groceries",
                amount_range=(1, 3),
                pickup_duration=(15, 35),
                dropoff_duration=(20, 40),
                travel_buffer=(15, 45),
                turnaround_buffer=(10, 35),
            ),
            ServiceProfile(
                name="retail",
                amount_range=(2, 5),
                pickup_duration=(20, 45),
                dropoff_duration=(25, 50),
                travel_buffer=(20, 60),
                turnaround_buffer=(15, 40),
            ),
            ServiceProfile(
                name="priority",
                amount_range=(1, 2),
                pickup_duration=(10, 25),
                dropoff_duration=(15, 30),
                travel_buffer=(10, 35),
                turnaround_buffer=(5, 25),
            ),
            ServiceProfile(
                name="bulk",
                amount_range=(4, 7),
                pickup_duration=(30, 60),
                dropoff_duration=(35, 70),
                travel_buffer=(30, 75),
                turnaround_buffer=(20, 45),
            ),
        ]

        return [self.generate_query(services_per_query, profiles) for _ in range(num_queries)]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Generate naturalistic VRP-LU queries")
    parser.add_argument(
        "--num-queries",
        type=int,
        default=DEFAULT_NUM_QUERIES,
        help="Number of queries to generate",
    )
    parser.add_argument(
        "--services-per-query",
        type=int,
        default=DEFAULT_SERVICES_PER_QUERY,
        help="Number of pickup-dropoff pairs per query",
    )
    parser.add_argument(
        "--output",
        type=str,
        default="generated_queries_natural.txt",
        help="Output file path",
    )
    parser.add_argument(
        "--seed",
        type=int,
        default=None,
        help="Random seed for reproducibility",
    )
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    rng = random.Random(args.seed)
    generator = NaturalQueryGenerator(rng)
    queries = generator.generate_queries(args.num_queries, args.services_per_query)

    with open(args.output, "w", encoding="utf-8") as f:
        f.write("\n\n".join(queries))

    print(
        f"Created {args.num_queries} queries with {args.services_per_query} services each. "
        f"Saved to '{args.output}'."
    )


if __name__ == "__main__":
    main()
