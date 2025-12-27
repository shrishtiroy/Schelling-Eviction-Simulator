# Forced Displacement and Residential Segregation

A Schelling-style agent-based simulation that studies how forced displacement, modeled through evictions, affects residential segregation and homophily.

## Overview

This project extends the classic Schelling segregation model by introducing eviction dynamics that forcibly displace agents from the grid. The goal is to analyze whether eviction leads to increased integration or increased segregation among displaced and non-displaced populations.

The simulation tracks homophily ratios over time to measure how spatial clustering changes under different eviction rates and probabilities.

## The Schelling Process

The base model follows the standard Schelling segregation framework:

* Agents belong to one of two classes
* Agents occupy positions on a grid
* Each agent evaluates satisfaction based on the number of same-class neighbors
* Unsatisfied agents relocate to new positions
* Repeated iterations lead to emergent segregation patterns

This project preserves this process while layering forced displacement on top of voluntary movement.

## Eviction Extension

Evictions are introduced as an external shock affecting a specific class of agents.

Two parameters control eviction behavior:

* **Eviction rate:** the fraction of a class displaced during an eviction event
* **Eviction probability:** the chance an eviction occurs during a simulation round

When an eviction occurs:
* Agents from the targeted class are removed from their current locations
* Evicted agents are relocated elsewhere on the grid
* The simulation resumes using standard Schelling movement rules

Homophily ratios are computed separately for the evicted and non-evicted classes.

## Experiments

Experiments were run with:

* Two agent classes
* Small, medium, and large populations
* Fixed grid density per population size
* Multiple trials per parameter configuration

For each population size:
* Eviction rate was varied while eviction probability was held constant
* Eviction probability was varied while eviction rate was held constant

Average homophily ratios were recorded for each class across trials.

## Results

Key observations:

* The evicted class generally exhibits increased homophily after displacement
* The non-evicted class shows little change or slight decreases in homophily
* Higher eviction probabilities tend to increase segregation more than higher eviction rates
* Some parameter combinations reduce segregation, indicating sensitivity to model settings

Compared to the no-eviction baseline, forced displacement consistently alters segregation dynamics.

## Interpretation

The simulation suggests that forced displacement often worsens segregation. Displaced agents tend to cluster together rather than integrate into existing neighborhoods, while non-evicted agents remain relatively stable.

## Conclusion

Introducing evictions into a Schelling-style model significantly changes segregation outcomes. In most cases, forced displacement increases homophily among displaced agents, leading to more segregated spatial patterns.
