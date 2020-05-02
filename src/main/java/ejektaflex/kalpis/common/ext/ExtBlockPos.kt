package ejektaflex.kalpis.common.ext

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d


fun BlockPos.vec3d(): Vec3d {
    return Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
}

fun BlockPos.toBox(): Box {
    return Box(vec3d(), vec3d().add(Vec3d(1.0, 1.0, 1.0)))
}

operator fun BlockPos.plus(other: BlockPos): BlockPos {
    return add(other.x, other.y, other.z)
}