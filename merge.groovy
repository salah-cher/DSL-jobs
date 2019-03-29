job('DDV_Merge_DQA') {
    scm {
        git {
            remote {
                name('origin')
                url('https://github.com/user/puppet_mgmt_automation.git')
                credentials('jenkinsAccessBlueOcean')
            }
            
            branch('DDV')
            extensions {
                mergeOptions {
                    remote('origin')
                    branch('DQA')
                }
            }
        }
    }
}
