defmodule Day4 do
  @doc ~S|
      iex> Day4.part1("""
      ...> [1518-04-12 00:36] Guard #112 begins shift
      ...> [1518-04-12 00:48] falls asleep
      ...> [1518-04-12 00:52] wakes up
      ...> [1518-04-13 00:30] Guard #112 begins shift
      ...> [1518-04-13 00:51] falls asleep
      ...> [1518-04-13 00:52] wakes up
      ...> """)
      %{id: 112, min: 51}
  |
  def part1(input \\ get_input()) do
    lines =
      input
      |> String.split("\n", trim: true)
      |> Enum.sort()

    sleepiest_guard =
      lines
      |> parse_lines_to_events()
      |> get_naps()
      |> get_sleep_min_counts()
      |> get_sleepiest_guard()

    # For each guard, figure out how often they were asleep at each minute
    %{id: sleepiest_guard.id, min: get_sleepiest_min(sleepiest_guard.counts)}
  end

  def part2(input \\ get_input()) do
    lines =
      input
      |> String.split("\n", trim: true)
      |> Enum.sort()

    lines
    |> parse_lines_to_events()
    |> get_naps()
    |> get_sleep_min_counts()
    |> Enum.map(fn {id, counts} ->
      {min, count} = Enum.max_by(counts, fn {_min, count} -> count end)
      {id, min, count}
    end)
    |> Enum.max_by(fn {_id, _min, count} -> count end)
  end

  @doc """
    iex> Day4.get_sleep_min_counts([
    ...>   %{id: 829, start: 15, end: 18},
    ...>   %{id: 829, start: 16, end: 20},
    ...>   %{id: 42, start: 1, end: 2},
    ...> ])
    %{
      829 => %{15 => 1, 16 => 2, 17 => 2, 18 => 1, 19 => 1},
      42 => %{1 => 1}
     }
  """
  def get_sleep_min_counts(naps) do
    naps
    |> Enum.reduce(%{}, fn nap, min_counts_by_id ->
      min_counts_by_id
      |> update_in([nap.id], fn counts ->
        Range.new(nap.start, nap.end - 1)
        |> Enum.reduce(counts || %{}, fn min, counts ->
          counts
          |> update_in([min], &((&1 || 0) + 1))
        end)
      end)
    end)
  end

  @doc """
    iex> Day4.get_sleepiest_guard(%{
    ...>   829 => %{15 => 1, 16 => 2, 17 => 2, 18 => 1, 19 => 1},
    ...>   42 => %{1 => 1}
    ...> })
    %{id: 829, counts: %{15 => 1, 16 => 2, 17 => 2, 18 => 1, 19 => 1}}
  """
  def get_sleepiest_guard(sleep_min_counts) do
    {id, counts} =
      sleep_min_counts
      |> Enum.max_by(fn {_id, counts} ->
        counts
        |> Map.values()
        |> Enum.sum()
      end)

    %{id: id, counts: counts}
  end

  @doc """
    iex> Day4.get_sleepiest_min(%{
    ...>   15 => 1,
    ...>   16 => 2,
    ...>   17 => 2,
    ...>   18 => 1,
    ...>   19 => 1
    ...> })
    16
  """
  def get_sleepiest_min(counts) do
    counts
    |> Enum.max_by(fn {_min, count} -> count end)
    |> elem(0)
  end

  @doc """
    iex> Day4.parse_lines_to_events([
    ...>   "[1518-04-12 00:11] Guard #829 begins shift",
    ...>   "[1518-04-12 00:36] falls asleep",
    ...>   "[1518-04-12 00:58] wakes up"
    ...> ])
    [{:shift_start, 829}, {:fall_asleep, 36}, {:wake_up, 58}]
  """
  def parse_lines_to_events(lines) do
    shift_begins_re = ~r/Guard #(\d+) begins shift/
    sleep_re = ~r/(\d\d)\] (falls asleep|wakes up)/

    Enum.map(lines, fn line ->
      cond do
        Regex.match?(shift_begins_re, line) ->
          [_, id] = Regex.run(shift_begins_re, line)
          {:shift_start, String.to_integer(id)}

        Regex.match?(sleep_re, line) ->
          [_, min, type] = Regex.run(sleep_re, line)

          case type do
            "falls asleep" -> {:fall_asleep, String.to_integer(min)}
            "wakes up" -> {:wake_up, String.to_integer(min)}
          end

        true ->
          raise "Invalid line detected: #{line}"
      end
    end)
  end

  @doc """
    iex> Day4.get_naps([
    ...>   {:shift_start, 829},
    ...>   {:fall_asleep, 36},
    ...>   {:wake_up, 58}
    ...> ])
    [%{id: 829, start: 36, end: 58}]

    iex> Day4.get_naps([
    ...>   {:shift_start, 829},
    ...>   {:fall_asleep, 4},
    ...>   {:wake_up, 12},
    ...>   {:fall_asleep, 38},
    ...>   {:wake_up, 48}
    ...> ])
    [%{id: 829, start: 4, end: 12}, %{id: 829, start: 38, end: 48}]
  """
  def get_naps(events) do
    events
    |> Enum.reduce({[], nil}, fn event, {naps, cur_nap} ->
      case event do
        {:shift_start, id} -> {naps, %{id: id}}
        {:fall_asleep, min} -> {naps, Map.put(cur_nap, :start, min)}
        {:wake_up, min} -> {naps ++ [Map.put(cur_nap, :end, min)], %{id: cur_nap.id}}
        _ -> raise "Invalid event #{event}"
      end
    end)
    |> elem(0)
  end

  def get_input() do
    File.read!("priv/input.txt")
  end
end
