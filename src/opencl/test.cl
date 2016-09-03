// created on Jul 9, 2016

/**
 * @author Dylan
 */
kernel void test(global float* a, const int size) {
    int id = get_global_id(0);
    if (id >= size) {
        return;
    }
    a[id] = 0;
}