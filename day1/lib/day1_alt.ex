defmodule Day1Alt do
  @doc ~S|
  Run the exercise.

  ## Examples
      iex> Day1Alt.run("""
      ...> +2500
      ...> -2240
      ...> -148
      ...> """)
      2500

      iex> Day1Alt.run("""
      ...> +1
      ...> +1
      ...> -1
      ...> """)
      1
  |
  def run(input \\ File.read!("priv/input.txt")) do
    input
    |> String.trim("\n")
    |> String.split("\n")
    |> Enum.map(&String.to_integer/1)
    |> Stream.cycle()
    |> Enum.reduce_while({0, MapSet.new([0])}, fn delta, {prev_freq, seen} ->
      freq = prev_freq + delta

      if MapSet.member?(seen, freq) do
        {:halt, freq}
      else
        {:cont, {freq, MapSet.put(seen, freq)}}
      end
    end)
  end
end
