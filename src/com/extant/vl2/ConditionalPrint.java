package com.extant.vl2;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/* Documentation of java.util.Deque
 * add(element): Adds an element to the tail.
 * add(element): Adds an element to the tail.
 * addFirst(element): Adds an element to the head.
 * addLast(element): Adds an element to the tail.
 * offer(element): Adds an element to the tail and returns a boolean to explain if the insertion was successful.
 * offerFirst(element): Adds an element to the head and returns a boolean to explain if the insertion was successful.
 * offerLast(element): Adds an element to the tail and returns a boolean to explain if the insertion was successful.
 * iterator(): Return an iterator for this deque.
 * descendingIterator(): Returns an iterator that has the reverse order for this deque.
 * push(element): Adds an element to the head.
 * pop(element): Removes an element from the head and returns it.
 * removeFirst(): Removes the element at the head.
 * removeLast(): Removes the element at the tail.
 * add(element): Adds an element to the tail.
 * addFirst(element): Adds an element to the head.
 * addLast(element): Adds an element to the tail.
 * offer(element): Adds an element to the tail and returns a boolean to explain if the insertion was successful.
 * offerFirst(element): Adds an element to the head and returns a boolean to explain if the insertion was successful.
 * offerLast(element): Adds an element to the tail and returns a boolean to explain if the insertion was successful.
 * iterator(): Return an iterator for this deque.
 * descendingIterator(): Returns an iterator that has the reverse order for this deque.
 * push(element): Adds an element to the head.
 * pop(element): Removes an element from the head and returns it.
 * removeFirst(): Removes the element at the head.
 * removeLast(): Removes the element at the tail.
 * addFirst(element): Adds an element to the head.
 * addLast(element): Adds an element to the tail.
 * offer(element): Adds an element to the tail and returns a boolean to explain if the insertion was successful.
 * offerFirst(element): Adds an element to the head and returns a boolean to explain if the insertion was successful.
 * offerLast(element): Adds an element to the tail and returns a boolean to explain if the insertion was successful.
 * iterator(): Return an iterator for this deque.
 * descendingIterator(): Returns an iterator that has the reverse order for this deque.
 * push(element): Adds an element to the head.
 * pop(element): Removes an element from the head and returns it.
 * removeFirst(): Removes the element at the head.
 * removeLast(): Removes the element at the tail.
 */

public class ConditionalPrint
{
	int reportLevel;
	Deque deque = new LinkedList<ChartElement>();

	public void setReportLevel(int level)
	{
		reportLevel = level;
	}

	public void maybePrint(ChartElement element)
	{

	}

	private boolean push(ChartElement element)
	{
		return deque.offerFirst(element);
	}

	private ChartElement pop()
	{
		if (deque.isEmpty())
			return null;
		return (ChartElement) deque.removeFirst();
	}

	private ChartElement[] listQueue()
	{
		Iterator iterator = deque.iterator();
		ChartElement[] list = new ChartElement[10];
		int i = 0;
		while (iterator.hasNext())
		{
			list[i++] = (ChartElement) iterator.next();
		}
		// System.out.println("\t" + iterator.next());
		return list;
	}

}
