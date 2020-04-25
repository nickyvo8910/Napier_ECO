package ea;


import teamPursuit.*;

public class Individual {

	boolean[] transitionStrategy = new boolean[22];
	int[] pacingStrategy = new int[23];

	SimulationResult result = null;
	double timeElapse = 0.0;

	public Individual() {

	}

	// this code just evolves the transition strategy
	// an individual is initialised with a random strategy that will evolve
	// the pacing strategy is initialised to the default strategy and remains fixed

	public void initialise() {
		
		
		double step = 0;
		int optimumGuess = 325;
		for (int i = 0; i < transitionStrategy.length; i++) {
			transitionStrategy[i] = Parameters.rnd.nextBoolean();
			//Minimising consecutive transitions
			if (i >= 1) {
				if (transitionStrategy[i-1] == true)
					transitionStrategy[i] = false;
			}
		}

		for (int i = 0; i < pacingStrategy.length; i++) {
			pacingStrategy[i] = Parameters.DEFAULT_WOMENS_PACING_STRATEGY[i];
			step = Parameters.rnd.nextGaussian();
			while (Math.abs(step) > 1)
				step = Parameters.rnd.nextGaussian();
			if (pacingStrategy[i] * (1 + step) <= 1200 && pacingStrategy[i] * (1 + step) >= 200)
				pacingStrategy[i] = (int) (pacingStrategy[i] * (1 + step));

			else
				pacingStrategy[i] = optimumGuess;

		}

	}

	// this is just there in case you want to check the default strategies
	public void initialise_default() {
		for (int i = 0; i < transitionStrategy.length; i++) {
			transitionStrategy[i] = Parameters.DEFAULT_WOMENS_TRANSITION_STRATEGY[i];
		}

		for (int i = 0; i < pacingStrategy.length; i++) {
			pacingStrategy[i] = Parameters.DEFAULT_WOMENS_PACING_STRATEGY[i];
		}

	}

	public void evaluate(TeamPursuit teamPursuit) {
		try {
			result = teamPursuit.simulate(transitionStrategy, pacingStrategy);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// this is a very basic fitness function
	// if the race is not completed, the chromosome gets fitness 1000
	// otherwise, the fitness is equal to the time taken
	// chromosomes that don't complete all get the same fitness (i.e regardless of
	// whether they
	// complete 10% or 90% of the race

//	public double getFitness() {
//		double fitness = 1000;
//		if (result == null || result.getProportionCompleted() < 0.999) {
//			return fitness;
//		} else {
//			fitness = result.getFinishTime();
//		}
//		return fitness;
//	}

	double mean(double[] m) {
		double sum = 0;
		for (int i = 0; i < m.length; i++) {
			sum += m[i];
		}
		return sum / m.length;
	}

	public double getFitness() {
		double fitness = 1000;
		if (result != null) {
			if (result.getProportionCompleted() >= 0.999) {
				fitness = result.getFinishTime();
//				System.out.println("Completed: Remain ="+mean(result.getEnergyRemaining()));
//				System.out.println("Completed: Mean velocity ="+mean(result.getVelocityProfile()));
//				System.out.println(result.toString());
			}

			else {
//				System.out.println("Halted: Remain ="+result.getEnergyRemaining()[result.getEnergyRemaining().length-1]);
//				System.out.println("Not Completed: Mean velocity ="+mean(result.getVelocityProfile()));
//				System.out.println("Time elapsed:" + result.getFinishTime());
//				System.out.println("Velocity Profile: "+ result.getVelocityProfile());
//				fitness = (3000 * (2 - result.getProportionCompleted()))
//						/ ((1 + result.getProportionCompleted()) * mean(result.getVelocityProfile()));
//				3000*(2-result.getProportionCompleted())
//				(1+result.getProportionCompleted())*mean(result.getVelocityProfile())

				// Fitness as penalty
				// proportional with wasted energy (log is to dampen the effect and 1 is to
				// avoid log(0))
				// disproportionate with percentage of the race completed
				// padding =300 penalty points for not completing
				
//				System.out.println(result.toString());
				double wastedEnergy=0.0;
				for (int i = 0; i < result.getEnergyRemaining().length; i++) {
					wastedEnergy += result.getEnergyRemaining()[i];
				}
				fitness = 300 + 10*Math.log10(1 + wastedEnergy)
						/ (result.getProportionCompleted());

			}
		}
		return fitness;
	}

	public Individual copy() {
		Individual individual = new Individual();
		for (int i = 0; i < transitionStrategy.length; i++) {
			individual.transitionStrategy[i] = transitionStrategy[i];
		}
		for (int i = 0; i < pacingStrategy.length; i++) {
			individual.pacingStrategy[i] = pacingStrategy[i];
		}
		individual.evaluate(EA.teamPursuit);
		return individual;
	}

	@Override
	public String toString() {
		String str = "";
		if (result != null) {
			str += getFitness();
		}
		return str;
	}

	public void print() {
		for (int i : pacingStrategy) {
			System.out.print(i + ",");
		}
		System.out.println();
		for (boolean b : transitionStrategy) {
			if (b) {
				System.out.print("true,");
			} else {
				System.out.print("false,");
			}
		}
		System.out.println("\r\n" + this);
	}
}
