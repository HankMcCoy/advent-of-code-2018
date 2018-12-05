const fs = require("fs");

const add = (a, b) => a + b;
const getInput = () => fs.readFileSync("./input.txt", "utf8");

const shouldReact = (a, b = "") => a != b && a.toLowerCase() == b.toLowerCase();

function runReactions(prevPolymer) {
  let nextPolymer = "";
  let i = 0;
  while (i < prevPolymer.length) {
    if (shouldReact(prevPolymer[i], prevPolymer[i + 1])) {
      i += 2;
    } else {
      nextPolymer += prevPolymer[i];
      i += 1;
    }
  }
  return nextPolymer.trim();
}

function iterateReactions(polymer) {
  nextPolymer = runReactions(polymer);
  return nextPolymer == polymer ? nextPolymer : iterateReactions(nextPolymer);
}

function part1() {
  console.log("Part 1:", iterateReactions(getInput()).length);
}

function removeUnit(polymer, unit) {
  const isOtherUnit = curUnit => curUnit.toLowerCase() != unit.toLowerCase();
  return polymer
    .split("")
    .filter(isOtherUnit)
    .join("");
}

function part2() {
  const polymer = getInput();
  let minLength = Number.MAX_SAFE_INTEGER;
  for (var i = 65; i < 90; i++) {
    const unit = String.fromCharCode(i);
    const length = iterateReactions(removeUnit(polymer, unit)).length;

    minLength = Math.min(length, minLength);
  }

  console.log("Part 2:", minLength);
}

part1();
part2();
