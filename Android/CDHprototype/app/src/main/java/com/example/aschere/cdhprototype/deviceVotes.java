package com.example.aschere.cdhprototype;

import com.example.aschere.cdhprototype.exceptions.InvalidVoterIdException;

/**
 * Created by aschere on 11/7/2015.
 * Represents a device's vote of confidence on other devices
 * So voter1 has votes on voter2 and voter3
 * voter2 has votes on voter1 and voter3
 * voter3 has votes on voter1 and voter2
 * No voter can vote on itself, it can only vote on the others.
 * Once a voter has been initialized, it can't change its voterId.
 * <p/>
 * Provided methods:
 * -getVote
 * -setVote
 * -getVoterId
 * -flipVote (optional)
 * <p/>
 * Hmm, maybe make it singleton-ish to ensure only one device of vote1, one of vote2, etc?
 */
public class deviceVotes
{
	public final static boolean CONFIDENT_VOTE = true; //so its true if the device has confidence in another; false otherwise
	private boolean vote1;
	private boolean vote2;
	private boolean vote3;
	private int voterId;

	/**
	 * @param voterId this device's voter id
	 * @throws InvalidVoterIdException
	 */
	public deviceVotes(int voterId) throws InvalidVoterIdException
	{
		this.voterId = voterId;
		switch (this.voterId)
		{ //start with confidence on other devices
			case 1:
				vote2 = CONFIDENT_VOTE;
				vote3 = CONFIDENT_VOTE;
			case 2:
				vote1 = CONFIDENT_VOTE;
				vote3 = CONFIDENT_VOTE;
			case 3:
				vote1 = CONFIDENT_VOTE;
				vote2 = CONFIDENT_VOTE;
			default:
				throw new InvalidVoterIdException("Voter ID must be of 1, 2, or 3!");
		}
	}

	/**
	 * Returns the confidence of this device on the device with voterID == voteId
	 *
	 * @param voteId the device we want to know the confidence of
	 * @return !CONFIDENT_VOTE if this device thinks voteId device is malfunctioning, CONFIDENT_VOTE otherwise
	 * @throws InvalidVoterIdException voteId is not of {1, 2, 3} or trying to get confidence on itself
	 */
	public boolean getVote(int voteId) throws InvalidVoterIdException
	{
		if (voteId != this.voterId)
		{
			switch (voteId)
			{
				case 1:
					return vote1;
				case 2:
					return vote2;
				case 3:
					return vote3;
				default:
					throw new InvalidVoterIdException("Trying to get invalid confidence vote!");
			}
		}
		else
		{
			throw new InvalidVoterIdException("Trying to get confidence vote of itself!");
		}
	}

	/**
	 * @return this device's voteId
	 */
	public int getVoterId()
	{
		return this.voterId;
	}

	/**
	 * @param voterId the voterId to change to
	 */
	private void setVoterId(int voterId) throws InvalidVoterIdException
	{
		if (0 < voterId && voterId < 4)
		{
			this.voterId = voterId;
		}
		throw new InvalidVoterIdException("Invalid voterId!");
	}

	/**
	 * @param voteId the device we want to change the confidence of
	 * @param vote   our confidence on the device
	 * @throws InvalidVoterIdException
	 */
	public void setVote(int voteId, boolean vote) throws InvalidVoterIdException
	{
		if (voteId != this.voterId)
		{
			switch (voteId)
			{
				case 1:
					this.vote1 = vote;
				case 2:
					this.vote2 = vote;
				case 3:
					this.vote3 = vote;
				default:
					throw new InvalidVoterIdException("Trying to set invalid confidence vote!");
			}
		}
		else
		{
			throw new InvalidVoterIdException("Trying to set confidence vote on itself!");
		}
	}

	/**
	 * If confident on the device, make it not so
	 * Or vice versa
	 * More efficient than setVote, I suppose
	 *
	 * @param voteId the device to flip the confidence on
	 * @throws InvalidVoterIdException
	 */
	public void flipVote(int voteId) throws InvalidVoterIdException
	{
		if (voteId != this.voterId)
		{
			switch (voteId)
			{
				case 1:
					this.vote1 = !this.vote1;
				case 2:
					this.vote2 = !this.vote2;
				case 3:
					this.vote3 = !this.vote3;
				default:
					throw new InvalidVoterIdException("Trying to flip confidence vote of invalid index!");
			}
		}
		else
		{
			throw new InvalidVoterIdException("Trying to flip confidence vote of itself!");
		}
	}
}