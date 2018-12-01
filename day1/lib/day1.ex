defmodule Day1 do
  @doc ~S|
  Run the exercise.

  ## Examples
      iex> Day1.run("""
      ...> +2500
      ...> -2240
      ...> -148
      ...> """)
      2500
  |
  def run(input \\ get_input()) do
    freqs = input |> get_frequency_deltas() |> convert_deltas_to_freqs()
    cycle = List.last(freqs)

    # Compare each f to every other f and see if the diff between them is evenly divisible by the cycle
    get_earliest_repeated_frequency(freqs, cycle)
  end

  def get_earliest_repeated_frequency(freqs, cycle, start_idx \\ 0, earliest_repeat \\ nil)

  def get_earliest_repeated_frequency([hd | tail], cycle, start_idx, earliest_repeat) do
    earliest_repeat =
      List.foldl(Enum.with_index(tail), earliest_repeat, fn {f, idx}, acc ->
        case {get_collision({hd, start_idx}, {f, idx}, cycle), acc} do
          {nil, _} ->
            acc

          {collision, nil} ->
            collision

          {collision, _} ->
            cond do
              collision.num_cycles < acc.num_cycles -> collision
              collision.num_cycles == acc.num_cycles and collision.idx < acc.idx -> collision
              true -> acc
            end
        end
      end)

    get_earliest_repeated_frequency(tail, cycle, start_idx + 1, earliest_repeat)
  end

  def get_earliest_repeated_frequency([], _cycle, _start_idx, earliest_repeat) do
    case earliest_repeat do
      nil -> raise "No repeated frequency found!"
      %{freq: freq} -> freq
    end
  end

  @doc """
  Determine if two frequencies will eventually collide and return the collision frequency.

  ## Examples
    iex> Day1.get_collision({2500, 13}, {260, 80}, 112)
    %{freq: 2500, num_cycles: 20, idx: 80}

    iex> Day1.get_collision({-150, 100}, {-13, 3}, -18)
    nil

    iex> Day1.get_collision({-150, 100}, {-13, 3}, -137)
    %{freq: -150, num_cycles: 1, idx: 3}
  """
  def get_collision({f1, idx1}, {f2, idx2}, cycle) do
    d = f1 - f2

    case rem(d, cycle) do
      0 ->
        %{
          freq: if(cycle > 0, do: max(f1, f2), else: min(f1, f2)),
          num_cycles: abs(div(d, cycle)),
          idx: if(f1 / cycle > f2 / cycle, do: idx2, else: idx1)
        }

      _ ->
        nil
    end
  end

  @doc ~S|
  Get numeric deltas from a string.

  ## Examples
    iex> Day1.get_frequency_deltas("""
    ...> +2500
    ...> -2240
    ...> -148
    ...> """)
    [2500, -2240, -148]
  |
  def get_frequency_deltas(input) do
    input |> String.split("\n") |> Enum.drop(-1) |> Enum.map(&String.to_integer/1)
  end

  @doc """
  Convert a list of deltas to a list of absolute values.

  ## Examples
      iex> Day1.convert_deltas_to_freqs([2500, -2240, -148])
      [2500, 260, 112]
  """
  def convert_deltas_to_freqs(deltas) do
    List.foldl(deltas, [], fn delta, acc -> acc ++ [convert_delta(delta, List.last(acc))] end)
  end

  def get_input() do
    File.read!("priv/input.txt")
  end

  @doc """
  Combine a delta w/ the previous freq to get the next freq.

  ## Examples
      iex> Day1.convert_delta(-118, 200)
      82

      iex> Day1.convert_delta(-118, nil)
      -118
  """
  def convert_delta(delta, prevFreq) do
    case prevFreq do
      nil -> delta
      _ -> delta + prevFreq
    end
  end
end
