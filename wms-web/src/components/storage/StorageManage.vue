<script>
import Header from "@/components/Header.vue";
import Aside from "@/components/Aside.vue";

export default {
  name: "StorageManage",
  data()
  {
    return {
      tableData: [],
      pageSize:17,
      pageNum:1,
      total:0,
      name:'',
      centerDialogVisible:false,
      form:{
        no:'',
        name:'',
        remark:''
      },
      rules: {
        name: [
          {required: true, message: '请输入仓库名', trigger: 'blur'},
        ]
      }
    }
  },
  methods:{
    del(id){
      console.log(id)
      this.$axios.get(this.$httpUrl+'/storage/delete?id='+id).then(res=>res.data).then(res=>{
        console.log(res)
        if(res.code === 200){
          this.$message({
            message:'删除成功',
            type:'success'
          })
          this.loadPost()
        } else {
          this.$message({
            message:'删除失败',
            type: 'warning'
          })
        }
      })
    },
    mod(row){
      console.log(row)
      this.form.id = row.id
      this.form.name = row.name
      this.form.remark = row.remark
      this.centerDialogVisible = true
    },
    resetForm() {
      this.$refs.form.resetFields();
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
      this.pageSize=val
      this.loadPost()
    },
    handleCurrentChange(val) {
      console.log(`当前页: ${val}`);
      this.pageNum = val;
      this.loadPost()
    },
    resetParam(){
      this.name = ''
    },
    loadPost(){
      this.$axios.post(this.$httpUrl+'/storage/listPage',{
        pageSize:this.pageSize,
        pageNum:this.pageNum,
        param:{
          name:this.name,
        }
      }).then(res=>res.data).then(res=>{
        console.log(res)
        if(res.code === 200){
          this.tableData = res.data
          this.total = res.total
        } else {
          alert("获取失败")
        }
      })
    },
    add(){
      this.centerDialogVisible = true
      this.$nextTick(()=>{
        this.resetForm();
      })
    },
    doSave(){
      this.$axios.post(this.$httpUrl+'/storage/save',this.form).then(res=>res.data).then(res=>{
        console.log(res)
        if(res.code === 200){
          this.$message({
            message:'操作成功',
            type:'success'
          })
          this.centerDialogVisible = false
          this.loadPost()
          this.resetForm()
        } else {
          this.$message({
            message:'操作失败',
            type: 'warning'
          })
        }
      })
    },
    doMod(){
      this.$axios.post(this.$httpUrl+'/storage/update',this.form).then(res=>res.data).then(res=>{
        console.log(res)
        if(res.code === 200){
          this.tableData = res.data
          this.total = res.total
          this.$message.success("修改成功！")
          this.loadPost()
        } else {
          this.$message.error("修改失败！")
        }
      })
    },
    sava(){
      this.$refs.form.validate((valid) => {
        if (valid) {//表单校验是否通过
          if(this.form.id){
            this.doMod();
          }else {
            this.doSave();
          }
        } else {
          alert("添加失败，请输入正确的格式！")
          return false;
        }
      });


    },
    headerStyle(){
      // console.log(Index)
      return 'background:rgb(245, 246, 247);text-align:center;'
    }
  },
  beforeMount() {
    // this.loadGet();
    this.loadPost();
  }
};

</script>

<template>
  <div>
    <div style="margin-bottom: 5px;">
      <el-input v-model="name" placeholder="请输入仓库名" suffix-icon="el-icon-search" style="width: 200px;"
                @keyup.enter.native="loadPost"></el-input>
      <el-button type="primary" style="margin-left: 5px;" @click="loadPost">查询</el-button>
      <el-button type="success" @click="resetParam">重置</el-button>
      <el-button type="primary" @click="add">新增</el-button>
    </div>
    <el-table :data="tableData"
              :header-cell-style="headerStyle"
              border

    >
      <el-table-column fixed prop="id" label="ID" min-width="60">
      </el-table-column>
      <el-table-column prop="name" label="仓库名" min-width="180">
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="180">
      </el-table-column>

      <el-table-column align="center" fixed="right" prop="remark" label="操作" min-width="180" >
        <template slot-scope="scope">
          <el-button size="small"  type="primary" @click="mod(scope.row)">编辑</el-button>
          <el-popconfirm
              title="确定删除吗？"
              @confirm="del(scope.row.id)"
              style="margin-left: 10px;"
          >
            <el-button slot="reference" size="small"  type="danger">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        style="text-align: center;margin-bottom: 15px;"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[2,5,13,17]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>

    <el-dialog
        title="提示"
        :visible.sync="centerDialogVisible"
        width="30%"
        center>
      <el-form ref="form" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="仓库名" prop="name">
          <el-col :span="20">
            <el-input v-model="form.name"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-col :span="20">
            <el-input type="textarea" v-model="form.remark"></el-input>
          </el-col>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="centerDialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="sava">确 定</el-button>
    </span>
    </el-dialog>
  </div>



</template>

<style scoped>

</style>