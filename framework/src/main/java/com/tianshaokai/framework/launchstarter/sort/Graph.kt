package com.tianshaokai.framework.launchstarter.sort

import java.util.*
import kotlin.collections.ArrayList

class Graph constructor(private val mVerticeCount: Int) {
    //邻接表
    private val mAdj = arrayOfNulls<ArrayList<Int>>(mVerticeCount)

    init {
        for (i in 0 until mVerticeCount) {
            mAdj[i] = ArrayList()
        }
    }

    /**
     * 添加边
     *
     * @param u from
     * @param v to
     */
    fun addEdge(u: Int, v: Int) {
        mAdj[u]?.add(v)
    }

    fun topologicalSort(): Vector<Int> {
        val indegree = IntArray(mVerticeCount)
        for (i in 0 until mVerticeCount) { //初始化所有点的入度数量
            val temp = mAdj[i]
            temp?.apply {
                forEach {
                    indegree[it]++
                }
            }
        }

        val queue: Queue<Int> = LinkedList()
        for (i in 0 until mVerticeCount) { //找出所有入度为0的点
            if (indegree[i] == 0) {
                queue.add(i)
            }
        }
        var cnt = 0
        val topOrder = Vector<Int>()
        while (!queue.isEmpty()) {
            val u = queue.poll() ?: break
            topOrder.add(u)
            for (node in mAdj[u]!!) { //找到该点（入度为0）的所有邻接点
                if (--indegree[node] == 0) { //把这个点的入度减一，如果入度变成了0，那么添加到入度0的队列里
                    queue.add(node)
                }
            }
            cnt++
        }
        check(cnt == mVerticeCount) {
            //检查是否有环，理论上拿出来的点的次数和点的数量应该一致，如果不一致，说明有环
            "Exists a cycle in the graph"
        }
        return topOrder
    }
}